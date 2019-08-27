package io.rsbox.server.net.message.handshake

import io.rsbox.server.net.Message

/**
 * @author Kyle Escobar
 */

data class HandshakeSeedResponse(val seed: Long) : Message