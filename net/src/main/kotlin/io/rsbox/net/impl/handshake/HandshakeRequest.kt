package io.rsbox.net.impl.handshake

import io.rsbox.net.Message

/**
 * @author Kyle Escobar
 */

data class HandshakeRequest(val type: Int, val revision: Int) : Message