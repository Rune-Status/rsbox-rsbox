package io.rsbox.engine.sync.segment

import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.game.packet.GamePacketBuilder
import io.rsbox.engine.sync.SyncSegment

/**
 * @author Kyle Escobar
 */

class AddLocalPlayerSegment(private val other: Player, private val locationSegment: PlayerLocationHashSegment?) : SyncSegment {

    override fun encode(buf: GamePacketBuilder) {
        buf.putBits(1, 1)
        buf.putBits(2, 0)

        buf.putBits(1, if(locationSegment != null) 1 else 0)

        locationSegment?.encode(buf)

        buf.putBits(13, other.tile.x and 0x1FFF)
        buf.putBits(13, other.tile.z and 0x1FFF)
        buf.putBits(1, 1)
    }

}