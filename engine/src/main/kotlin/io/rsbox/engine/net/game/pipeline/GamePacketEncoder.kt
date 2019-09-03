package io.rsbox.engine.net.game.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.engine.net.game.packet.GamePacket
import io.rsbox.engine.net.game.packet.PacketType
import io.rsbox.util.IsaacRandom
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class GamePacketEncoder(private val random: IsaacRandom) : MessageToByteEncoder<GamePacket>() {

    override fun encode(ctx: ChannelHandlerContext, msg: GamePacket, out: ByteBuf) {
        if(msg.type == PacketType.VARIABLE_BYTE && msg.length >= 256) {
            logger.error("Packet length for type 'variable-byte' must be less than 256. [length=${msg.length}]")
            return
        }

        if(msg.type == PacketType.VARIABLE_SHORT && msg.length >= 65536) {
            logger.error("Packet length for type 'variable-short' but be less than 65535. [length=${msg.length}]")
            return
        }

        out.writeByte((msg.opcode + random.nextInt()) and 0xFF)

        when(msg.type) {
            PacketType.VARIABLE_BYTE -> out.writeByte(msg.length)
            PacketType.VARIABLE_SHORT -> out.writeShort(msg.length)
            else -> {}
        }

        out.writeBytes(msg.payload)
        msg.payload.release()
    }

    companion object : KLogging()
}