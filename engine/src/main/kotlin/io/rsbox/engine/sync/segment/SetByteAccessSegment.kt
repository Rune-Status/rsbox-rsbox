package io.rsbox.engine.sync.segment

import io.rsbox.engine.net.game.packet.GamePacketBuilder
import io.rsbox.engine.sync.SyncSegment

/**
 * @author Kyle Escobar
 */

class SetByteAccessSegment : SyncSegment {

    override fun encode(buf: GamePacketBuilder) {
        buf.switchToByteAccess()
    }

}