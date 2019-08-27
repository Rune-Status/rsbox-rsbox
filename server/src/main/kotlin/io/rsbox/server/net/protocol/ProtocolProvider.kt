package io.rsbox.server.net.protocol

import io.rsbox.server.net.protocol.impl.HandshakeProtocol
import io.rsbox.server.net.protocol.impl.JS5Protocol
import io.rsbox.server.net.protocol.impl.LoginProtocol

/**
 * @author Kyle Escobar
 */

class ProtocolProvider {

    val handshake = HandshakeProtocol()

    val js5 = JS5Protocol()

    val login = LoginProtocol()

}