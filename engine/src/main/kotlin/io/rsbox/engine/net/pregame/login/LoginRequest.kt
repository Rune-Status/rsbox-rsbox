package io.rsbox.engine.net.pregame.login

import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Session

/**
 * @author Kyle Escobar
 */

data class LoginRequest(
    val username: String,
    val password: String,
    val revision: Int,
    val xteaKeys: IntArray,
    val authCode: Int,
    val reconnecting: Boolean,
    val clientResizable: Boolean,
    val clientWidth: Int,
    val clientHeight: Int,
    val session: Session
) : Message