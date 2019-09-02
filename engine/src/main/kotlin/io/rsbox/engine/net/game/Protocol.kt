package io.rsbox.engine.net.game

import java.lang.Exception

/**
 * @author Kyle Escobar
 */

open class Protocol {

    private val inboundPackets = PacketRegistry()
    private val outboundPackets = PacketRegistry()

    fun inbound(opcode: Int, packet: Class<out Packet>) {
        inboundPackets.bind(opcode, packet)
    }

    fun outbound(opcode: Int, packet: Class<out Packet>) {
        outboundPackets.bind(opcode, packet)
    }

    fun getInboundMessage(opcode: Int): Packet {
        return inboundPackets.getMessage(opcode) ?: throw Exception("Unable to find inbound opcode $opcode.")
    }
}