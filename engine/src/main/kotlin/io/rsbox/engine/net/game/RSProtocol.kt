package io.rsbox.engine.net.game

import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.exception.IllegalOpcodeException
import io.rsbox.engine.net.game.impl.decoder.IgnoredDecoder
import io.rsbox.engine.net.game.impl.encoder.RegionRebuildEncoder
import io.rsbox.engine.net.game.impl.handler.IgnoredHandler
import io.rsbox.engine.net.game.impl.message.IgnoredMessage
import io.rsbox.engine.net.game.impl.message.RegionRebuildMessage
import io.rsbox.engine.net.game.packet.*
import mu.KLogging

/**
 * @author Kyle Escobar
 */

object RSProtocol : KLogging() {

    private val codecs = CodecSet(256)

    fun init() {
        // Outbound Packets
        outbound(0, RegionRebuildMessage::class.java, RegionRebuildEncoder())

        // Inbound Packets
        inbound(255, IgnoredMessage::class.java, IgnoredDecoder(), IgnoredHandler())
    }


    private fun <M : Message, D : MessageDecoder<M>, H : MessageHandler<M>> inbound(opcode: Int, message: Class<M>, decoder: D, handler: H) {
        codecs.bindInbound(opcode, message, decoder, handler)
    }

    private fun <M : Message, E : MessageEncoder<M>> outbound(opcode: Int, message: Class<M>, encoder: E) {
        codecs.bindOutbound(opcode, message, encoder)
    }

    @Suppress("UNCHECKED_CAST")
    fun <M : Message> encodeMessage(message: M): GamePacket {
        val opcode = codecs.getOutboundOpcode(message::class.java)

        if(opcode == -1) {
            throw IllegalOpcodeException("Unable to encode outbound message ${message::class.java.simpleName} as it isn't bound to an opcode.")
        }

        val encoder = codecs.getOutboundEncoder(message::class.java) as MessageEncoder<M>? ?:
                throw IllegalOpcodeException("Unable to encode outbound message ${message::class.java.simpleName} as not encoder was found for opcode $opcode.")

        val length = ServerPackets.opcodeMap.getValue(opcode).length
        val type = when(length) {
            -1 -> PacketType.VARIABLE_BYTE
            -2 -> PacketType.VARIABLE_SHORT
            else -> PacketType.FIXED
        }

        val builder = GamePacketBuilder(opcode, type)
        return encoder.encode(message, builder)
    }

    fun isInboundOpcode(opcode: Int): Boolean {
        return codecs.getInboundDecoder(opcode) != null
    }

    fun decodePacket(packet: GamePacket): Message {
        val opcode = packet.opcode
        val decoder = codecs.getInboundDecoder(opcode) ?: throw IllegalOpcodeException("Unable to decode inbound packet with opcode $opcode.")

        val reader = GamePacketReader(packet)

        return decoder.decode(reader)
    }

    @Suppress("UNCHECKED_CAST")
    fun <M : Message> handleMessage(session: Session, opcode: Int, message: M) {
        val handler = codecs.getInboundHandler(opcode) as MessageHandler<M>? ?: throw IllegalOpcodeException("Unable to handle message ${message::class.java.simpleName} as no handler was bound.")
        handler.handle(session, message)
    }
}