package io.rsbox.net.impl.handshake

import io.rsbox.net.LoginState
import io.rsbox.net.Message

/**
 * @author Kyle Escobar
 */

data class HandshakeResponse(val state: LoginState) : Message