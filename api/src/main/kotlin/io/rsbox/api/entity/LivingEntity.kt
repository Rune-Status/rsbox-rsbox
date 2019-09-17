package io.rsbox.api.entity

import io.rsbox.api.Engine
import io.rsbox.api.RSBox
import io.rsbox.api.World

/**
 * @author Kyle Escobar
 */

interface LivingEntity : Entity {

    var index: Int

    val engine: Engine get() = RSBox.engine

    val world: World get() = RSBox.world

}