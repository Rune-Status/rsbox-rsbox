package io.rsbox.engine.net.game

/**
 * @author Kyle Escobar
 */

open class Protocol {

    private val inboundMessages = MessageRegistry()
    private val outboundMessages = MessageRegistry()

    fun inbound(opcode: Int, message: Class<out Message>) {
        inboundMessages.bind(opcode, message)
    }

    fun outbound(opcode: Int, message: Class<out Message>) {
        outboundMessages.bind(opcode, message)
    }
}