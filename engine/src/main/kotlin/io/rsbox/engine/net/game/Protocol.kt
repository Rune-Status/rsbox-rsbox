package io.rsbox.engine.net.game

import java.lang.Exception

/**
 * @author Kyle Escobar
 */

open class Protocol {

    private val inboundMessages = PacketRegistry()
    private val outboundMessages = PacketRegistry()

    fun inbound(opcode: Int, message: Class<out Packet>) {
        inboundMessages.bind(opcode, message)
    }

    fun outbound(opcode: Int, message: Class<out Packet>) {
        outboundMessages.bind(opcode, message)
    }

    fun getInboundMessage(opcode: Int): Packet {
        return inboundMessages.getMessage(opcode) ?: throw Exception("Unable to find inbound opcode $opcode.")
    }
}