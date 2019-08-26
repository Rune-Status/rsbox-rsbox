package io.rsbox.server.net.message.handshake

import io.rsbox.server.net.Message

/**
 * @author Kyle Escobar
 */

data class HandshakeRequest(val type: Int, val revision: Int = -1) : Message