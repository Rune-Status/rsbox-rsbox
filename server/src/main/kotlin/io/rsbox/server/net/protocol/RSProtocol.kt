package io.rsbox.server.net.protocol

import io.netty.buffer.ByteBuf
import io.rsbox.server.net.Codec
import io.rsbox.server.net.Message
import io.rsbox.server.net.MessageHandler
import io.rsbox.server.net.registry.CodecRegistry
import io.rsbox.server.net.registry.HandlerRegistry
import io.rsbox.server.net.session.GameSession
import mu.KLogging

/**
 * @author Kyle Escobar
 */

/**
 * Defines a protocol used for interacting with OSRS clients.
 *
 * @param name The protocol name for reference.
 */
abstract class RSProtocol(override val name: String, private val highestOpcode: Int) :
    Protocol {

    private val inboundCodecs = CodecRegistry(highestOpcode + 1)
    private val outboundCodecs = CodecRegistry(highestOpcode + 1)
    private val handlers = HandlerRegistry()

    /**
     * Associates an opcode, message, codec, and handler to each other.
     * Specific to this protocol subtype.
     *
     * @param opcode The opcode integer.
     * @param message The message class.
     * @param codec The codec class.
     * @param handler The handler class.
     */
    fun <M : Message, C : Codec<in M>, H : MessageHandler<GameSession, in M>> inbound(
        opcode: Int,
        message: Class<M>,
        codec: Class<C>,
        handler: Class<H>
    ) {
        try {
            inboundCodecs.bind(opcode, message, codec)
            handlers.bind(message, handler)
        } catch(e : Exception) {
            logger.error("Error registering inbound opcode $opcode.", e)
        }
    }

    /**
     * Associates an opcode, message, and codec to each other.
     * Specific to this protocol subtype.
     *
     * @param opcode The opcode integer.
     * @param message The message class.
     * @param codec The codec class.
     */
    fun <M : Message, C : Codec<in M>> outbound(
        opcode: Int,
        message: Class<M>,
        codec: Class<C>
    ) {
        try {
            outboundCodecs.bind(opcode, message, codec)
        } catch(e : Exception) {
            logger.error("Error registering outbound opcode $opcode.", e)
        }
    }

    /**
     * Used to retrieve a message handler given a message class.
     *
     * @param message The message class.
     * @return The associated handler object.
     */
    @Suppress("UNCHECKED_CAST")
    fun <M : Message> getMessageHandler(message: Class<M>): MessageHandler<GameSession, M> {
        val handler = handlers.find(message)
        return handler as MessageHandler<GameSession, M>
    }

    abstract fun readOpcode(buf: ByteBuf): Int

    /**
     * Retrieves the codec given a buffer. This methods executes readOpcode()
     * to figure out what codec is required for that opcode.
     *
     * @param buf The inbound message buffer.
     * @return The associated codec.
     */
    fun getInboundCodec(buf: ByteBuf): Codec<*> {
        val opcode = readOpcode(buf)
        return inboundCodecs.find(opcode)
    }

    /**
     * Retrieves a codec given a message class.
     *
     * @param message The message class.
     * @return The associated codec.
     */
    fun <M : Message> getOutboundCodec(message: Class<M>): Codec<M> {
        return outboundCodecs.find(message).getCodec()
    }

    companion object : KLogging()
}