package io.rsbox.engine.sync.segment

import io.rsbox.engine.game.model.Tile
import io.rsbox.engine.game.model.block.PlayerBlockType
import io.rsbox.engine.game.model.entity.Player
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

    }

}