package io.rsbox.server.net.protocol.impl

import io.netty.buffer.ByteBuf
import io.rsbox.server.net.codec.js5.JS5InboundCodec
import io.rsbox.server.net.codec.js5.JS5OutboundCodec
import io.rsbox.server.net.handler.js5.JS5Handler
import io.rsbox.server.net.message.js5.JS5Request
import io.rsbox.server.net.message.js5.JS5Response
import io.rsbox.server.net.protocol.RSProtocol

/**
 * @author Kyle Escobar
 */

/**
 * The protocol that handles downloading the cache when the client first connects.
 */
class JS5Protocol : RSProtocol("JS5", 6) {

    init {
        inbound(0, JS5Request::class.java, JS5InboundCodec::class.java, JS5Handler::class.java) // PRIORITY ARCHIVE REQUEST
        inbound(1, JS5Request::class.java, JS5InboundCodec::class.java, JS5Handler::class.java) // ARCHIVE REQUEST
        inbound(2, JS5Request::class.java, JS5InboundCodec::class.java, JS5Handler::class.java) // CLIENT STATUS (Ignored)
        inbound(3, JS5Request::class.java, JS5InboundCodec::class.java, JS5Handler::class.java) // CLIENT STATUS (Ignored)
        inbound(6, JS5Request::class.java, JS5InboundCodec::class.java, JS5Handler::class.java) // CLIENT STATUS (Ignored)

        outbound(0, JS5Response::class.java, JS5OutboundCodec::class.java)
    }

    override fun readOpcode(buf: ByteBuf): Int {
        buf.markReaderIndex()
        return buf.readByte().toInt()
    }

}