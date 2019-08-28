package io.rsbox.net.protocol

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.net.Message
import io.rsbox.net.MessageHandler
import io.rsbox.net.registry.CodecRegistry
import io.rsbox.net.registry.HandlerRegistry
import io.rsbox.net.session.Session
import mu.KLogging

/**
 * @author Kyle Escobar
 */

abstract class RSProtocol(override val name: String) : Protocol {

    private val inboundCodecs = CodecRegistry()
    private val outboundCodecs = CodecRegistry()
    private val handlers = HandlerRegistry()

    fun <M : Message, C : Codec<in M, *>, H : MessageHandler<*, in M>> inbound(
        opcode: Int,
        message: Class<M>,
        codec: Class<C>,
        handler: Class<H>
    ) {
        try {
            inboundCodecs.bindInbound(message, codec, opcode)
            handlers.bind(message, handler)
        } catch(e : Exception) {
            logger.error("Error registering inbound opcode $opcode in protocol $name.", e)
        }
    }

    fun <M : Message, C : Codec<*, in M>> outbound(
        opcode: Int,
        message: Class<M>,
        codec: Class<C>
    )  {
        try {
            outboundCodecs.bindOutbound(message, codec, opcode)
        } catch(e : Exception) {
            logger.error("Error registering outbound opcode $opcode in protocol $name.", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <M : Message> getMessageHandler(message: Class<M>): MessageHandler<Session, in M> {
        val handler = handlers.find(message)
        return handler as MessageHandler<Session, in M>
    }

    fun <M : Message> getCodecRegistration(messageClass: Class<M>): Codec.CodecRegistration {
        return outboundCodecs.find(messageClass)
    }

    fun getInboundCodec(opcode: Int): Codec<*, *> {
        return inboundCodecs.find(opcode)
    }

    fun <M : Message> getOutboundCodec(messageClass: Class<M>): Codec<*, M> {
        return outboundCodecs.find(messageClass).getCodec()
    }

    abstract override fun readOpcode(buf: ByteBuf): Int

    companion object : KLogging()
}