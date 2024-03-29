package io.rsbox.engine.game.model

import io.rsbox.api.Direction

/**
 * Credits to tomm for his Tile class.
 * Minor modifications for RSbox use.
 *
 * @author Kyle Escobar
 */

class Tile : io.rsbox.api.Tile {

    private val coordinate: Int

    override val x: Int get() = coordinate and 0x7FFF

    override val z: Int get() = (coordinate shr 15) and 0x7FFF

    override val height: Int get() = coordinate ushr 30

    override val asPackedInteger: Int get() = (z and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((height and 0x3) shl 28)

    override val asTileHashMultiplier: Int get() = (z shr 13) or ((x shr 13) shl 8) or ((height and 0x3) shl 16)

    private constructor(coordinate: Int) {
        this.coordinate = coordinate
    }

    constructor(x: Int, z: Int, height: Int = 0) : this((x and 0x7FFF) or ((z and 0x7FFF) shl 15) or (height shl 30))

    constructor(other: Tile) : this(other.x, other.z, other.height)

    fun isWithinRadius(otherX: Int, otherZ: Int, otherHeight: Int, radius: Int): Boolean {
        if(otherHeight != height) {
            return false
        }

        val dx = Math.abs(x - otherX)
        val dz = Math.abs(z - otherZ)
        return dx <= radius && dz <= radius
    }

    fun isWithinRadius(other: Tile, radius: Int): Boolean = isWithinRadius(other.x, other.z, other.height, radius)

    fun step(direction: Direction, num: Int = 1): Tile = Tile(this.x + (num * direction.getDeltaX()), this.z + (num * direction.getDeltaZ()), this.height)

}