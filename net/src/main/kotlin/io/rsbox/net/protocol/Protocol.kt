package io.rsbox.net.protocol

import io.netty.buffer.ByteBuf
import io.rsbox.net.session.Session

/**
 * @author Kyle Escobar
 */

interface Protocol {

    val name: String

    fun onSet(session: Session)

    fun onUnset(session: Session)

    fun readOpcode(buf: ByteBuf): Int { throw IllegalAccessError("Current protocol cannot read opcodes.") }

    fun readLength(buf: ByteBuf): Int { throw IllegalAccessError("Current protocol cannot read lengths.") }
}