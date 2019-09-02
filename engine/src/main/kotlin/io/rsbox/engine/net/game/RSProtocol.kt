package io.rsbox.engine.net.game

import io.rsbox.engine.net.game.packet.outbound.RegionLoadLoginPacket
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class RSProtocol: Protocol() {

    init {
        /**
         * Inbound Packets
         */

        /**
         * Outbound Packets
         */
        outbound(0, RegionLoadLoginPacket::class.java)
    }

    companion object : KLogging()
}