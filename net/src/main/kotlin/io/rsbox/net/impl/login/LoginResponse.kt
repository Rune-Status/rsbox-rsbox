package io.rsbox.net.impl.login

import io.rsbox.net.Message

/**
 * @author Kyle Escobar
 */

data class LoginResponse(val index: Int, val privilege: Int) : Message