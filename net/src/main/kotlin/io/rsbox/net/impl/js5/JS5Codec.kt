package io.rsbox.net.impl.js5

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec

/**
 * @author Kyle Escobar
 */

class JS5Codec : Codec<JS5Request, JS5Response> {

    override fun encode(buf: ByteBuf, message: JS5Response): ByteBuf {
        buf.writeByte(message.index)
        buf.writeShort(message.archive)

        message.data.forEach { data ->
            if(buf.writerIndex() % 512 == 0) {
                buf.writeByte(-1)
            }
            buf.writeByte(data.toInt())
        }

        return buf
    }

    override fun decode(buf: ByteBuf): JS5Request {
        buf.resetReaderIndex()
        buf.markReaderIndex()
        val opcode = buf.readByte().toInt()
        val type = JS5RequestType.values().firstOrNull { it.opcode == opcode }
        when(type) {
            JS5RequestType.CLIENT_STATUS_1, JS5RequestType.CLIENT_STATUS_2, JS5RequestType.CLIENT_STATUS_3 -> {
                buf.skipBytes(3)
            }

            JS5RequestType.ARCHIVE_PRIORITY, JS5RequestType.ARCHIVE_NORMAL -> {
                if(buf.readableBytes() < 3) {
                    buf.resetReaderIndex()
                } else {
                    val index = buf.readUnsignedByte().toInt()
                    val archive = buf.readUnsignedShort()
                    val priority = opcode == JS5RequestType.ARCHIVE_PRIORITY.opcode

                    return JS5Request(index, archive, priority)
                }
            }
        }

        return JS5Request(-1, -1, false)
    }
}