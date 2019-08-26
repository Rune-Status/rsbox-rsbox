package io.rsbox.server.net.protocol

import io.rsbox.server.net.protocol.impl.HandshakeProtocol

/**
 * @author Kyle Escobar
 */

class ProtocolProvider {

    val handshake = HandshakeProtocol()

}