package io.rsbox.server.net.protocol.impl

import io.netty.buffer.ByteBuf
import io.rsbox.server.net.codec.handshake.HandshakeInboundCodec
import io.rsbox.server.net.codec.handshake.HandshakeOutboundCodec
import io.rsbox.server.net.codec.handshake.HandshakeSeedOutboundCodec
import io.rsbox.server.net.handler.handshake.HandshakeHandler
import io.rsbox.server.net.message.handshake.HandshakeRequest
import io.rsbox.server.net.message.handshake.HandshakeResponse
import io.rsbox.server.net.message.handshake.HandshakeSeedResponse
import io.rsbox.server.net.protocol.RSProtocol

/**
 * @author Kyle Escobar
 */

/**
 * The handshake protocol tells the server what protocol the client is going to send next.
 * 15 = JS5
 * 14 = LOGIN
 */
class HandshakeProtocol : RSProtocol("HANDSHAKE", 15) {

    init {
        inbound(15, HandshakeRequest::class.java, HandshakeInboundCodec::class.java, HandshakeHandler::class.java) // JS5 Handshake
        inbound(14, HandshakeRequest::class.java, HandshakeInboundCodec::class.java, HandshakeHandler::class.java) // Login Handshake

        outbound(0, HandshakeResponse::class.java, HandshakeOutboundCodec::class.java)
        outbound(1, HandshakeSeedResponse::class.java, HandshakeSeedOutboundCodec::class.java)
    }

    override fun readOpcode(buf: ByteBuf): Int {
        return buf.readUnsignedByte().toInt()
    }

}