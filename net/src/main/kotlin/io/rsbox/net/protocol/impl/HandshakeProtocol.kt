package io.rsbox.net.protocol.impl

import io.netty.buffer.ByteBuf
import io.rsbox.net.impl.handshake.*
import io.rsbox.net.protocol.RSProtocol
import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

class HandshakeProtocol : RSProtocol("HANDSHAKE") {

    init {
        // JS5 Handshake
        inbound(15, HandshakeRequest::class.java, HandshakeCodec::class.java, HandshakeHandler::class.java)
        // Login Handshake
        inbound(14, HandshakeRequest::class.java, HandshakeCodec::class.java, HandshakeHandler::class.java)

        outbound(0, HandshakeResponse::class.java, HandshakeCodec::class.java)
        outbound(1, HandshakeSeedResponse::class.java, HandshakeSeedCodec::class.java)
    }

    override fun readOpcode(buf: ByteBuf): Int {
        return buf.readUnsignedByte().toInt()
    }

    override fun onSet(session: Session) {} // Unused for handshake protocol
    override fun onUnset(session: Session) {} // Unused for handshake protocol
}