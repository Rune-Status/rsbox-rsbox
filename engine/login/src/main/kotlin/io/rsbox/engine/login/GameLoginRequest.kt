package io.rsbox.engine.login

/**
 * @author Kyle Escobar
 */

data class GameLoginRequest(
    val username: String,
    val password: String,
    val revision: Int,
    val xteaKeys: IntArray,
    val viewportResizable: Boolean,
    val viewportWidth: Int,
    val viewportHeight: Int,
    val authCode: Int,
    val uuid: String,
    val reconnecting: Boolean,
    val seed: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameLoginRequest

        if (username != other.username) return false
        if (password != other.password) return false
        if (revision != other.revision) return false
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