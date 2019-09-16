package io.rsbox.engine.game.model.block

/**
 * @author Kyle Escobar
 */

class PlayerBlockState {

    private var mask = 0

    var teleport = false

    var faceDegrees = 0
    var faceLivingEntityIndex = -1

    var animation = 0
    var animationDelay = 0

    var graphicId = 0
    var graphicHeight = 0
    var graphicDelay = 0

    var forceMovement: Any? = null

    var hits = mutableListOf<Any>()

    fun isDirty(): Boolean = mask != 0

    fun clean() {
        mask = 0
        teleport = false
        hits.clear()
    }

    fun addBit(bit: Int) {
        mask = mask or bit
    }

    fun hasBit(bit: Int): Boolean {
        return (mask and bit) != 0
    }

    fun blockValue(): Int = mask
}