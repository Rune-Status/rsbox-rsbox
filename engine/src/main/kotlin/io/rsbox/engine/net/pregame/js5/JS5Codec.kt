package io.rsbox.engine.net.pregame.js5

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class JS5Codec : MessageToMessageCodec<ByteBuf, JS5Response>() {

    override fun encode(ctx: ChannelHandlerContext, msg: JS5Response, out: MutableList<Any>) {
        val buf = Unpooled.buffer()
        buf.writeByte(msg.index)
        buf.writeShort(msg.archive)

        msg.data.forEach { data ->
            if(buf.writerIndex() % 512 == 0) {
                buf.writeByte(-1)
            }
            buf.writeByte(data.toInt())
        }

        out.add(buf)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        if(!msg.isReadable) return

        msg.markReaderIndex()
        val opcode = msg.readByte().toInt()
        val requestType = JS5RequestType.values().firstOrNull { it.opcode == opcode }
        when(requestType) {
            JS5RequestType.CLIENT_STATUS_1, JS5RequestType.CLIENT_STATUS_2, JS5RequestType.CLIENT_STATUS_3 -> {
                msg.skipBytes(3)
            }

            JS5RequestType.ARCHIVE_NORMAL, JS5RequestType.ARCHIVE_PRIORITY -> {
                if(msg.readableBytes() >= 3) {
                    val index = msg.readUnsignedByte().toInt()
                    val archive = msg.readUnsignedShort()

                    val request = JS5Request(
                        index = index,
                        archive = archive,
                        priority = opcode == JS5RequestType.ARCHIVE_PRIORITY.opcode
                    )
                    out.add(request)
                } else {
                    msg.resetReaderIndex()
                }
            }

            else -> {
                logger.warn("Unhandled JS5 opcode $opcode.")
            }
        }
    }

    companion object : KLogging()
}