package io.rsbox.net.protocol.impl

import io.netty.buffer.ByteBuf
import io.rsbox.net.impl.js5.JS5Codec
import io.rsbox.net.impl.js5.JS5Handler
import io.rsbox.net.impl.js5.JS5Request
import io.rsbox.net.impl.js5.JS5Response
import io.rsbox.net.protocol.RSProtocol
import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

class JS5Protocol : RSProtocol("JS5") {

    init {
        inbound(0, JS5Request::class.java, JS5Codec::class.java, JS5Handler::class.java) // Priority Archive Request
        inbound(1, JS5Request::class.java, JS5Codec::class.java, JS5Handler::class.java) // Normal Archive request.
        inbound(2, JS5Request::class.java, JS5Codec::class.java, JS5Handler::class.java) // Client status - Ignored
        inbound(3, JS5Request::class.java, JS5Codec::class.java, JS5Handler::class.java) // Client status - Ignored
        inbound(6, JS5Request::class.java, JS5Codec::class.java, JS5Handler::class.java) // Client status - Ignored

        outbound(0, JS5Response::class.java, JS5Codec::class.java)
    }

    override fun readOpcode(buf: ByteBuf): Int {
        buf.markReaderIndex()
        return buf.readByte().toInt()
    }

    override fun onSet(session: Session) {}
    override fun onUnset(session: Session) {}

}