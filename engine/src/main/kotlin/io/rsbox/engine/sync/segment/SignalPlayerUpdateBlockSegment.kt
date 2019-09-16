package io.rsbox.engine.sync.segment

import io.rsbox.engine.net.game.packet.GamePacketBuilder
import io.rsbox.engine.sync.SyncSegment

/**
 * @author Kyle Escobar
 */

class SignalPlayerUpdateBlockSegment : SyncSegment {

    override fun encode(buf: GamePacketBuilder) {
        buf.putBits(1, 1)
        buf.putBits(1, 1)
        buf.putBits(2, 0)
    }

}