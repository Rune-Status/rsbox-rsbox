package io.rsbox.api.entity

import io.rsbox.api.Tile

/**
 * @author Kyle Escobar
 */

interface Entity {

    val tile: Tile get() = _tile()

    fun _tile(): Tile

}