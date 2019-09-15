package io.rsbox.engine.game.model.entity

import io.rsbox.api.Direction
import io.rsbox.engine.game.model.Tile
import io.rsbox.engine.game.model.block.PlayerBlockState

/**
 * @author Kyle Escobar
 */

open class LivingEntity : Entity(), io.rsbox.api.entity.LivingEntity {

    override var index = -1

    var blockState = PlayerBlockState()

    var invisible = false

    var moved = false

    val movementQueue: Any? = null

    var steps: Any? = null

    var direction: Direction = Direction.SOUTH

    var lastTile: Tile? = null
}