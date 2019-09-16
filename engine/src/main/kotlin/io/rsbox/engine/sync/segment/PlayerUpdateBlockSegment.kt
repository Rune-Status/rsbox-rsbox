package io.rsbox.engine.sync.segment

import io.rsbox.engine.game.model.Tile
import io.rsbox.engine.game.model.block.PlayerBlockType
import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.game.packet.DataOrder
import io.rsbox.engine.net.game.packet.DataTransformation
import io.rsbox.engine.net.game.packet.DataType
import io.rsbox.engine.net.game.packet.GamePacketBuilder
import io.rsbox.engine.sync.SyncSegment

/**
 * @author Kyle Escobar
 */

class PlayerUpdateBlockSegment(private val other: Player, private val newPlayer: Boolean) : SyncSegment {

    private val blockEncodeOrder = arrayOf(
        PlayerBlockType.HITMARK,
        PlayerBlockType.GFX,
        PlayerBlockType.MOVEMENT,
        PlayerBlockType.FORCE_MOVEMENT,
        PlayerBlockType.FORCE_CHAT,
        PlayerBlockType.FACE_TILE,
        PlayerBlockType.APPEARANCE,
        PlayerBlockType.FACE_ENTITY,
        PlayerBlockType.PUBLIC_CHAT,
        PlayerBlockType.ANIMATION
    )

    override fun encode(buf: GamePacketBuilder) {
        var mask = other.blockState.blockValue()

        var forceFaceEntity = false
        var forceFaceTile = false

        var forceFace: Tile? = null

        if(newPlayer) {
            mask = mask or PlayerBlockType.APPEARANCE.bit

            when {
                other.blockState.faceDegrees != 0 -> {
                    mask = mask or PlayerBlockType.FACE_TILE.bit
                    forceFaceTile = true
                }

                other.blockState.faceLivingEntityIndex != -1 -> {
                    mask = mask or PlayerBlockType.FACE_ENTITY.bit
                    forceFaceEntity = true
                }

                else -> {
                    mask = mask or PlayerBlockType.FACE_TILE.bit
                    forceFace = other.tile.step(other.direction)
                }
            }
        }

        if(mask >= 0x100) {
            mask = mask or PlayerBlockType.mask
            buf.put(DataType.BYTE, mask and 0xFF)
            buf.put(DataType.BYTE, mask shr 8)
        } else {
            buf.put(DataType.BYTE, mask and 0xFF)
        }

        /**
         * Loop through all the update blocks for the player and encode the data into the packet if
         * the player needs that block updated.
         */
        blockEncodeOrder.forEach { block ->
            val force = when(block) {
                PlayerBlockType.FACE_TILE -> forceFaceTile || forceFace != null
                PlayerBlockType.FACE_ENTITY -> forceFaceEntity
                PlayerBlockType.APPEARANCE -> newPlayer
                else -> false
            }

            if(other.hasBlock(block) || force) {
                encodeBlock(buf, block, forceFace)
            }
        }
    }

    private fun encodeBlock(buf: GamePacketBuilder, block: PlayerBlockType, forceFace: Tile?) {

        when(block) {

            PlayerBlockType.HITMARK -> {}

            PlayerBlockType.GFX -> {
                buf.put(
                    type = DataType.SHORT,
                    order = DataOrder.LITTLE,
                    transformation = DataTransformation.NONE,
                    value = other.blockState.graphicId
                )

                buf.put(
                    type = DataType.INT,
                    order = DataOrder.MIDDLE,
                    transformation = DataTransformation.NONE,
                    value = (other.blockState.graphicHeight shl 16) or other.blockState.graphicDelay
                )
            }

            PlayerBlockType.MOVEMENT -> {
                buf.put(
                    type = DataType.BYTE,
                    order = DataOrder.BIG,
                    transformation = DataTransformation.ADD,
                    value = (if(other.blockState.teleport) 127 else 1)
                )
            }

            PlayerBlockType.FORCE_MOVEMENT -> {}

            PlayerBlockType.FORCE_CHAT -> {}

            PlayerBlockType.FACE_TILE -> {
                if(forceFace != null) {
                    val sx = other.tile.x * 64
                    val sz = other.tile.z * 64
                    val dx = forceFace.x * 64
                    val dz = forceFace.z * 64
                    val degreesX = (sx - dx).toDouble()
                    val degreesZ = (sz - dz).toDouble()
                    buf.put(
                        type = DataType.SHORT,
                        order = DataOrder.LITTLE,
                        transformation = DataTransformation.NONE,
                        value = ((Math.atan2(degreesX, degreesZ) * 325.949).toInt() and 0x7ff)
                    )
                } else {
                    buf.put(
                        type = DataType.SHORT,
                        order = DataOrder.LITTLE,
                        transformation = DataTransformation.NONE,
                        value = other.blockState.faceDegrees
                    )
                }
            }

            PlayerBlockType.APPEARANCE -> {
                val appBuf = GamePacketBuilder()
                appBuf.put(DataType.BYTE, other.appearance.gender)
                appBuf.put(DataType.BYTE, -1)
                appBuf.put(DataType.BYTE, -1)

                // TODO add player appears as entity support.

                val translation = arrayOf(-1, -1, -1, -1, 2, -1, 3, 5, 0, 4, 6, 1)
                val animations = intArrayOf(808, 823, 819, 820, 821, 822, 824)

                val arms = 6
                val hair = 8
                val beard = 11

                // TODO add equipment rendering support.

                for(i in 0 until 12) {
                    if(i == arms) {}
                    else if(i == hair) {}

                    if(translation[i] == -1) {
                        appBuf.put(DataType.BYTE, 0)
                    } else {
                        appBuf.put(DataType.SHORT, 0x100 + other.appearance.looks[translation[i]])
                    }
                }

                for(i in 0 until 5) {
                    val color = Math.max(0, other.appearance.colors[i])
                    appBuf.put(DataType.BYTE, color)
                }

                animations.forEach { anim ->
                    appBuf.put(DataType.SHORT, anim)
                }

                appBuf.putString(other.displayName)
                appBuf.put(DataType.BYTE, 3)
                appBuf.put(DataType.SHORT, 0)
                appBuf.put(DataType.BYTE, 0)

                /**
                 * Load the appearance buffer into the update packet buffer.
                 */
                buf.put(
                    type = DataType.BYTE,
                    order = DataOrder.BIG,
                    transformation = DataTransformation.SUBTRACT,
                    value = appBuf.readableBytes
                )

                buf.putBytes(
                    transformation = DataTransformation.ADD,
                    buffer = appBuf.byteBuf
                )
            }

            PlayerBlockType.FACE_ENTITY -> {}

            PlayerBlockType.PUBLIC_CHAT -> {}

            PlayerBlockType.ANIMATION -> {
                buf.put(
                    type = DataType.SHORT,
                    order = DataOrder.BIG,
                    transformation = DataTransformation.NONE,
                    value = other.blockState.animation
                )

                buf.put(
                    type = DataType.BYTE,
                    order = DataOrder.BIG,
                    transformation = DataTransformation.NONE,
                    value = other.blockState.animationDelay
                )
            }

            else -> throw RuntimeException("Unhandled update block type $block.")
        }

    }

}