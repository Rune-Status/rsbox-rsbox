package io.rsbox.engine.net.pregame.js5

import io.rsbox.engine.net.Message

/**
 * @author Kyle Escobar
 */

data class JS5Response(val index: Int, val archive: Int, val data: ByteArray) : Message {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JS5Response

        if (index != other.index) return false
        if (archive != other.archive) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + archive
        result = 31 * result + data.contentHashCode()
        return result
    }
}