package io.rsbox.server.net.message.handshake

import io.rsbox.net.Message
import io.rsbox.server.net.ServerResponseType

/**
 * @author Kyle Escobar
 */

data class HandshakeResponse(val type: ServerResponseType) : Message