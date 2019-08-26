package io.rsbox.server.net.codec.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.net.Codec
import io.rsbox.server.net.message.handshake.HandshakeRequest

/**
 * @author Kyle Escobar
 */

class HandshakeInboundCodec : Codec<HandshakeRequest> {

    override fun decode(buf: ByteBuf): HandshakeRequest {
        buf.resetReaderIndex()

        val opcode = buf.readUnsignedByte().toInt()
        return when(opcode) {
            15 -> decodeJS5Handshake(buf)
            else -> HandshakeRequest(-1)
        }
    }

    private fun decodeJS5Handshake(buf: ByteBuf): HandshakeRequest {
        val revision = buf.readInt()
        return HandshakeRequest(15, revision)
    }

}