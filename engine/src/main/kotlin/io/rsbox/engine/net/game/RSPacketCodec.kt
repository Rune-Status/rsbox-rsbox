package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.model.GamePacket
import io.rsbox.engine.net.game.model.GamePacketBuilder
import io.rsbox.engine.net.game.model.GamePacketReader
import io.rsbox.engine.net.game.model.PacketType
import io.rsbox.engine.net.game.packet.ClientPackets

/**
 * @author Kyle Escobar
 */

class RSPacketCodec(val session: Session) : MessageToMessageCodec<ByteBuf, Packet>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer()

        msg.packet = GamePacketBuilder(opcode = msg.opcode, type = msg.type)
        msg.encode()

        val packet = msg.packet.toGamePacket()

        if(msg.type == PacketType.VARIABLE_BYTE && packet.length >= 256) {
            RSProtocol.logger.error("Packet length {} too long for type 'variable-byte' in session [${session.sessionId}].", packet.length)
            return
        }
        else if(msg.type == PacketType.VARIABLE_SHORT && packet.length >= 65536) {
            RSProtocol.logger.error("Packet length {} too long for type 'variable-short' in session [${session.sessionId}].", packet.length)
            return
        }

        buf.writeByte((msg.opcode + session.encodeRandom.nextInt()) and 0xFF)

        when(msg.type) {
            PacketType.VARIABLE_BYTE -> buf.writeByte(packet.length)
            PacketType.VARIABLE_SHORT -> buf.writeShort(packet.length)
            else -> {}
        }

        buf.writeBytes(packet.payload)
        packet.payload.release()

        out.add(buf)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if(!msg.isReadable) return

        val opcode = msg.readUnsignedByte().toInt() - (session.decodeRandom.nextInt()) and 0xFF
        val message = session.protocol.getInboundMessage(opcode)
        val type = message.type

        var length = 0

        when(type) {
            PacketType.FIXED -> {
                length = ClientPackets.values().first { it.opcode == opcode }.length
            }

            PacketType.VARIABLE_BYTE, PacketType.VARIABLE_SHORT -> {
                if(type == PacketType.VARIABLE_BYTE) {
                    length = msg.readUnsignedByte().toInt()
                }
                else if(type == PacketType.VARIABLE_SHORT) {
                    length = msg.readUnsignedShort()
                }
            }

            else -> throw Exception("Unhandled packet type $type for opcode $opcode.")
        }

        if(msg.readableBytes() < length) return

        val payload = msg.readBytes(length)
        message.reader = GamePacketReader(GamePacket(opcode, type, payload))
        message.decode()

        out.add(message)
    }
}