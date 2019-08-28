package io.rsbox.net.protocol.impl

import io.netty.buffer.ByteBuf
import io.rsbox.net.protocol.RSProtocol
import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

class JS5Protocol : RSProtocol("JS5") {


    override fun readOpcode(buf: ByteBuf): Int {
        buf.markReaderIndex()
        return buf.readByte().toInt()
    }

    override fun onSet(session: Session) {}
    override fun onUnset(session: Session) {}

}