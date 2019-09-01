package io.rsbox.api

/**
 * @author Kyle Escobar
 */

interface Tile {

    val x: Int

    val z: Int

    val height: Int

    val asPackedInteger: Int

    val asTileHashMultiplier: Int

}