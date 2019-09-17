package io.rsbox.engine.game.model.entity

import io.rsbox.engine.game.model.Tile

/**
 * @author Kyle Escobar
 */

open class Entity : io.rsbox.api.entity.Entity {

    override lateinit var tile: Tile

    override fun _tile(): io.rsbox.api.Tile = tile
}