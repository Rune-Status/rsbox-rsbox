package io.rsbox.engine.sync.segment

import io.rsbox.engine.net.game.packet.GamePacketBuilder
import io.rsbox.engine.sync.SyncSegment

/**
 * @author Kyle Escobar
 */

class PlayerSkipCountSegment(private val count: Int) : SyncSegment {

    override fun encode(buf: GamePacketBuilder) {
        buf.putBits(1, 0)

        when {
            count == 0 -> {
                buf.putBits(2, 0)
            }

            count < 32 -> {
                buf.putBits(2, 1)
                buf.putBits(5, count)
            }

            count < 256 -> {
                buf.putBits(2, 2)
                buf.putBits(8, count)
            }

            count < 2048 -> {
                buf.putBits(2, 3)
                buf.putBits(11, count)
            }
        }
    }
}