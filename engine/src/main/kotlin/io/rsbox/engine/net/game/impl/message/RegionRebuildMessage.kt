package io.rsbox.engine.net.game.impl.message

import io.rsbox.engine.game.model.Tile
import io.rsbox.engine.net.game.Message
import io.rsbox.engine.service.xtea.XteaKeyService

/**
 * @author Kyle Escobar
 */

data class RegionRebuildMessage(val tile: Tile, val xteaKeyService: XteaKeyService, val playerIndex: Int = -1, val tiles: IntArray? = null) : Message {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegionRebuildMessage

        if (tile != other.tile) return false
        if (xteaKeyService != other.xteaKeyService) return false
        if (playerIndex != other.playerIndex) return false
        if (tiles != null) {
            if (other.tiles == null) return false
            if (!tiles.contentEquals(other.tiles)) return false
        } else if (other.tiles != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tile.hashCode()
        result = 31 * result + xteaKeyService.hashCode()
        result = 31 * result + playerIndex
        result = 31 * result + (tiles?.contentHashCode() ?: 0)
        return result
    }
}