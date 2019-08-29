package io.rsbox.net.impl.login

import io.netty.channel.Channel
import io.rsbox.net.LoginState
import io.rsbox.net.Message

/**
 * @author Kyle Escobar
 */

data class LoginRequest(
    val username: String = "",
    val password: String = "",
    val revision: Int = -1,
    val channel: Channel? = null,
    val xteaKeys: IntArray = intArrayOf(),
    val viewportResizable: Boolean = false,
    val viewportWidth: Int = -1,
    val viewportHeight: Int = -1,
    val authCode: Int = -1,
    val uuid: String = "",
    val reconnecting: Boolean = false,
    val error: Boolean = false,
    val errorResponse: LoginState? = null
) : Message {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginRequest

        if (username != other.username) return false
        if (password != other.password) return false
        if (revision != other.revision) return false
        if (channel != other.channel) return false
        if (!xteaKeys.contentEquals(other.xteaKeys)) return false
        if (viewportResizable != other.viewportResizable) return false
        if (viewportWidth != other.viewportWidth) return false
        if (viewportHeight != other.viewportHeight) return false
        if (authCode != other.authCode) return false
        if (uuid != other.uuid) return false
        if (reconnecting != other.reconnecting) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + revision
        result = 31 * result + channel.hashCode()
        result = 31 * result + xteaKeys.contentHashCode()
        result = 31 * result + viewportResizable.hashCode()
        result = 31 * result + viewportWidth
        result = 31 * result + viewportHeight
        result = 31 * result + authCode
        result = 31 * result + uuid.hashCode()
        result = 31 * result + reconnecting.hashCode()
        return result
    }
}