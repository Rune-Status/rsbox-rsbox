package io.rsbox.server.net.protocol.impl

import io.netty.buffer.ByteBuf
import io.rsbox.server.net.protocol.RSProtocol

/**
 * @author Kyle Escobar
 */

class LoginProtocol : RSProtocol("LOGIN", 18) {

    init {

    }

    lateinit var loginType: LoginType

    override fun readOpcode(buf: ByteBuf): Int {
        val opcode = buf.readByte().toInt()
        loginType = when(opcode) {
            16 -> LoginType.NORMAL
            18 -> LoginType.RECONNECTING
            else -> throw Exception("Unhandled login type opcode $opcode.")
        }
        return opcode
    }

    enum class LoginType {
        RECONNECTING,
        NORMAL;
    }
}