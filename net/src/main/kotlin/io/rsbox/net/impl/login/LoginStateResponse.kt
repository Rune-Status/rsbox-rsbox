package io.rsbox.net.impl.login

import io.rsbox.net.LoginState
import io.rsbox.net.Message

/**
 * @author Kyle Escobar
 */

data class LoginStateResponse(val state: LoginState) : Message