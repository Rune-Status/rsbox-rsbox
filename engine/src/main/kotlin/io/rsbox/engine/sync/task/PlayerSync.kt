package io.rsbox.engine.sync.task

import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.game.packet.GamePacketBuilder
import io.rsbox.engine.net.game.packet.PacketType
import io.rsbox.engine.sync.SyncSegment
import io.rsbox.engine.sync.SyncTask
import io.rsbox.engine.sync.segment.*
import kotlin.RuntimeException

/**
 * @author Kyle Escobar
 */

object PlayerSync : SyncTask<Player> {

    private const val MAX_VIEWPORT_PLAYERS = 255

    private const val MAX_VIEWPORT_PLAYER_ADDITIONS = 64

    override fun execute(livingEntity: Player) {
        val player = livingEntity

        val buf = GamePacketBuilder(79, PacketType.VARIABLE_SHORT)
        val maskBuf = GamePacketBuilder()

        val segments = getSegments(player)
        for(segment in segments) {
            segment.encode(if(segment is PlayerUpdateBlockSegment) maskBuf else buf)
        }

        buf.putBytes(maskBuf.byteBuf)

        player.session.ctx.write(buf.toGamePacket())

        this.renderPlayerViewport(player)

    }

    private fun renderPlayerViewport(player: Player) {
        player.gpiLocalCount = 0
        player.gpiExternalCount = 0

        for(i in 1 until 2048) {
            if(player.gpiLocalPlayers[i] != null) {
                player.gpiLocalIndexes[player.gpiLocalCount++] = 1
            } else {
                player.gpiExternalIndexes[player.gpiExternalCount++] = 1
            }

            player.gpiInactivityFlags[i] = player.gpiInactivityFlags[i] shr 1
        }
    }

    private fun getSegments(player: Player): List<SyncSegment> {
        val segments = mutableListOf<SyncSegment>()

        segments.add(SetBitAccessSegment())
        addLocalSegments(player, true, segments)
        segments.add(SetByteAccessSegment())

        segments.add(SetBitAccessSegment())
        addLocalSegments(player, false, segments)
        segments.add(SetByteAccessSegment())

        var added = 0

        segments.add(SetBitAccessSegment())
        added += addExternalSegments(player, true, added, segments)
        segments.add(SetByteAccessSegment())

        segments.add(SetBitAccessSegment())
        added += addExternalSegments(player, false, added, segments)
        segments.add(SetByteAccessSegment())

        return segments
    }

    private fun addLocalSegments(player: Player, initial: Boolean, segments: MutableList<SyncSegment>) {
        var skipCount = 0

        for(i in 0 until player.gpiLocalCount) {
            val index = player.gpiLocalIndexes[i]
            val local = player.gpiLocalPlayers[index]

            val skip = when(initial) {
                true -> (player.gpiInactivityFlags[index] and 0x1) != 0
                else -> (player.gpiInactivityFlags[index] and 0x1) == 0
            }

            if(skip) continue

            if(skipCount > 0) {
                skipCount--
                player.gpiInactivityFlags[index] = player.gpiInactivityFlags[index] or 0x2
                continue
            }

            if(local != player && (local == null || shouldRemove(player, local))) {
                val lastTileHash = player.gpiTileHashMultipliers[index]
                val currentTileHash = local?.tile?.asTileHashMultiplier ?: 0
                val updateTileHash = lastTileHash != currentTileHash

                segments.add(RemoveLocalPlayerSegment(updateTileHash))

                if(updateTileHash) {
                    segments.add(PlayerLocationHashSegment(lastTileHash, currentTileHash))
                }

                player.gpiLocalPlayers[index] = null
                player.gpiTileHashMultipliers[index] = currentTileHash

                continue
            }

            val requiresBlockUpdate = local.blockState.isDirty()
            if(requiresBlockUpdate) {
                segments.add(PlayerUpdateBlockSegment(local, false))
            }

            if(local.moved) {
                segments.add(PlayerTeleportSegment(local, requiresBlockUpdate))
            }
            // TODO Add Player running.

            else if(requiresBlockUpdate) {
                segments.add(SignalPlayerUpdateBlockSegment())
            } else {
                for(j in i + 1 until player.gpiLocalCount) {
                    val nextIndex = player.gpiLocalIndexes[j]
                    val next = player.gpiLocalPlayers[nextIndex]

                    val skipNext = when(initial) {
                        true -> (player.gpiInactivityFlags[nextIndex] and 0x1) != 0
                        else -> (player.gpiInactivityFlags[nextIndex] and 0x1) == 0
                    }

                    if(skipNext) continue

                    if(next == null || next.blockState.isDirty() || next.moved || next.steps != null || next != player && shouldRemove(player, next)) {
                        break
                    }
                    skipCount++
                }
                segments.add(PlayerSkipCountSegment(skipCount))
                player.gpiInactivityFlags[index] = player.gpiInactivityFlags[index] or 0x2
            }
        }

        if(skipCount > 0) {
            throw RuntimeException("Unhandled player update skip count.")
        }
    }

    private fun addExternalSegments(player: Player, initial: Boolean, previouslyAdded: Int, segments: MutableList<SyncSegment>): Int {
        var skipCount = 0
        var added = previouslyAdded

        for(i in 0 until player.gpiExternalCount) {
            val index = player.gpiExternalIndexes[i]

            val skip = when(initial) {
                true -> (player.gpiInactivityFlags[index] and 0x1) == 0
                else -> (player.gpiInactivityFlags[index] and 0x1) != 0
            }

            if(skip) continue

            if(skipCount > 0) {
                skipCount--
                player.gpiInactivityFlags[index] = player.gpiInactivityFlags[index] or 0x2
                continue
            }

            val nonLocal = if(index < player.world.players.capacity) player.world.players[index] else null

            if(nonLocal != null && added < MAX_VIEWPORT_PLAYER_ADDITIONS && player.gpiLocalCount + added < MAX_VIEWPORT_PLAYERS
                && shouldAdd(player, nonLocal)) {
                val oldTileHash = player.gpiTileHashMultipliers[index]
                val currentTileHash = nonLocal.tile.asTileHashMultiplier

                val tileUpdateSegment = if(oldTileHash == currentTileHash) null else PlayerLocationHashSegment(oldTileHash, currentTileHash)

                segments.add(AddLocalPlayerSegment(nonLocal, tileUpdateSegment))
                segments.add(PlayerUpdateBlockSegment(nonLocal, true))

                player.gpiInactivityFlags[index] = player.gpiInactivityFlags[index] or 0x2
                player.gpiTileHashMultipliers[index] = currentTileHash
                player.gpiLocalPlayers[index] = nonLocal

                added++
                continue
            }

            for(j in i + 1 until player.gpiExternalCount) {
                val nextIndex = player.gpiExternalIndexes[j]

                val skipNext = when(initial) {
                    true -> (player.gpiInactivityFlags[nextIndex] and 0x1) == 0
                    else -> (player.gpiInactivityFlags[nextIndex] and 0x1) != 0
                }

                if(skipNext) continue

                val next = if(nextIndex < player.world.players.capacity) player.world.players[nextIndex] else null

                if(next != null && (shouldAdd(player, next) || next.tile.asTileHashMultiplier != player.gpiTileHashMultipliers[nextIndex])) {
                    break
                }
                skipCount++
            }
            segments.add(PlayerSkipCountSegment(skipCount))
            player.gpiInactivityFlags[index] = player.gpiInactivityFlags[index] or 0x2
        }

        if(skipCount > 0) {
            throw RuntimeException("Player external skip count unhandled.")
        }

        return added
    }

    private fun shouldAdd(player: Player, other: Player): Boolean {
        return !other.invisible && other.tile.isWithinRadius(player.tile, Player.LARGE_RENDER_DISTANCE) && other != player
    }

    private fun shouldRemove(player: Player, other: Player): Boolean {
        return !other.isOnline || other.invisible || !other.tile.isWithinRadius(player.tile, Player.LARGE_RENDER_DISTANCE)
    }

}