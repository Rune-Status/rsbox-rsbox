package io.rsbox.engine.net.pregame.login

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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginRequest

        if (username != other.username) return false
        if (password != other.password) return false
        if (revision != other.revision) return false
        if (!xteaKeys.contentEquals(other.xteaKeys)) return false
        if (authCode != other.authCode) return false
        if (reconnecting != other.reconnecting) return false
        if (clientResizable != other.clientResizable) return false
        if (clientWidth != other.clientWidth) return false
        if (clientHeight != other.clientHeight) return false
        if (session != other.session) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + revision
        result = 31 * result + xteaKeys.contentHashCode()
        result = 31 * result + authCode
        result = 31 * result + reconnecting.hashCode()
        result = 31 * result + clientResizable.hashCode()
        result = 31 * result + clientWidth
        result = 31 * result + clientHeight
        result = 31 * result + session.hashCode()
        return result
    }
}