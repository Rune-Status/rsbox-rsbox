package io.rsbox.net.protocol

import io.rsbox.net.protocol.impl.HandshakeProtocol
import io.rsbox.net.protocol.impl.JS5Protocol
import io.rsbox.net.protocol.impl.LoginProtocol

/**
 * @author Kyle Escobar
 */

class ProtocolProvider {

    val handshake = HandshakeProtocol()

    val js5 = JS5Protocol()

    val login = LoginProtocol()

}