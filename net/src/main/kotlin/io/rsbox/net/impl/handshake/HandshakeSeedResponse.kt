package io.rsbox.net.impl.handshake

import io.rsbox.net.Message

/**
 * @author Kyle Escobar
 */

data class HandshakeSeedResponse(val seed: Long) : Message