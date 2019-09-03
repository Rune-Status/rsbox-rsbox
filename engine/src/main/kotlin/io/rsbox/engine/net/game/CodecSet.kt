package io.rsbox.engine.net.game

/**
 * @author Kyle Escobar
 */

class CodecSet(val size: Int) {

    /**
     * Outbound use
     */
    private val encoders = hashMapOf<Class<out Message>, MessageEncoder<out Message>>()
    private val messages = hashMapOf<Class<out Message>, Int>()

    /**
     * Inbound use
     */
    private val decoders = arrayOfNulls<MessageDecoder<out Message>>(size)
    private val handlers = arrayOfNulls<MessageHandler<out Message>>(size)

    fun <T : Message> bindInbound(opcode: Int, message: Class<T>, decoder: MessageDecoder<T>, handler: MessageHandler<T>) {
        if(decoders[opcode] != null || handlers[opcode] != null) {
            throw Exception("Unable to bind ${message.simpleName} to inbound opcode $opcode as it is already bound.")
        }

        decoders[opcode] = decoder
        handlers[opcode] = handler
    }

    fun <T : Message> bindOutbound(opcode: Int, message: Class<T>, encoder: MessageEncoder<T>) {
        if(encoders[message] != null || !messages.containsKey(message)) {
            throw Exception("Unable to bind ${message.simpleName} to outbound opcode $opcode as it is already bound.")
        }

        encoders[message] = encoder
        messages[message] = opcode
    }

    fun getInboundDecoder(opcode: Int): MessageDecoder<out Message>? {
        return decoders[opcode]
    }

    fun getInboundHandler(opcode: Int): MessageHandler<out Message>? {
        return handlers[opcode]
    }

    fun getOutboundOpcode(message: Class<out Message>): Int {
        return messages[message] ?: -1
    }

    fun getOutboundEncoder(message: Class<out Message>): MessageEncoder<out Message>? {
        return encoders[message]
    }
}