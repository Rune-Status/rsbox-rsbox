package io.rsbox.net.impl.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec

/**
 * @author Kyle Escobar
 */

class HandshakeCodec : Codec<HandshakeRequest, HandshakeResponse> {

    override fun encode(buf: ByteBuf, message: HandshakeResponse): ByteBuf {
        buf.writeByte(message.state.id)
        return buf
    }

    override fun decode(buf: ByteBuf): HandshakeRequest {
        buf.resetReaderIndex()
        val opcode = buf.readUnsignedByte().toInt()

        return when(opcode) {
            HandshakeType.JS5.opcode -> decodeJS5Handshake(buf, opcode)
            HandshakeType.LOGIN.opcode -> decodeLoginHandshake(buf, opcode)
            else -> throw Exception("Unhandled handshake opcode $opcode.")
        }
    }

    private fun decodeJS5Handshake(buf: ByteBuf, opcode: Int): HandshakeRequest {
        if(buf.readableBytes() < 4) throw Exception("Unable to decode JS5 handshake packet.")

        val revision = buf.readInt()
        return HandshakeRequest(opcode, revision)
    }

    private fun decodeLoginHandshake(buf: ByteBuf, opcode: Int): HandshakeRequest {
        if(!buf.isReadable) {
            return HandshakeRequest(-1, -1)
        }

        return HandshakeRequest(opcode, -1)
    }
}