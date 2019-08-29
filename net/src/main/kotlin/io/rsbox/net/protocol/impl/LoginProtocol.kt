package io.rsbox.net.protocol.impl

import io.netty.buffer.ByteBuf
import io.rsbox.net.impl.login.*
import io.rsbox.net.protocol.RSProtocol
import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

class LoginProtocol : RSProtocol("LOGIN") {

    init {
        inbound(16, LoginRequest::class.java, LoginCodec::class.java, LoginHandler::class.java)
        inbound(18, LoginRequest::class.java, LoginCodec::class.java, LoginHandler::class.java)

        outbound(0, LoginResponse::class.java, LoginCodec::class.java)
        outbound(-1, LoginStateResponse::class.java, LoginStateCodec::class.java)
    }

    override fun readOpcode(buf: ByteBuf): Int {
        buf.markReaderIndex()
        return buf.readByte().toInt()
    }

    override fun onSet(session: Session) {}
    override fun onUnset(session: Session) {}
}