package io.rsbox.engine.sync.segment

import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.game.packet.GamePacketBuilder
import io.rsbox.engine.sync.SyncSegment

/**
 * @author Kyle Escobar
 */

class PlayerTeleportSegment(private val other: Player, private val updateBlocks: Boolean) : SyncSegment {

    override fun encode(buf: GamePacketBuilder) {
        buf.putBits(1, 1)
        buf.putBit(updateBlocks)
        buf.putBits(2, 3)

        val dx = other.tile.x - (other.lastTile?.x ?: 0)
        val dz = other.tile.z - (other.lastTile?.z ?: 0)
        val dh = other.tile.height - (other.lastTile?.height ?: 0)

        if(Math.abs(dx) <= Player.LARGE_RENDER_DISTANCE && Math.abs(dz) <= Player.LARGE_RENDER_DISTANCE) {
            buf.putBits(1, 0)

            buf.putBits(2, dh and 0x3)
            buf.putBits(5, dx and 0x1F)
            buf.putBits(5, dz and 0x1F)
        } else {
            buf.putBits(1, 1)

            buf.putBits(2, dh and 0x3)
            buf.putBits(14, dx and 0x3FFF)
            buf.putBits(14, dz and 0x3FFF)
        }
    }

}