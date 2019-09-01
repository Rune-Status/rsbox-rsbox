package io.rsbox.engine.game.model

import io.rsbox.engine.Engine
import io.rsbox.engine.game.model.entity.LivingEntityList
import io.rsbox.engine.game.model.entity.Player
import java.security.SecureRandom
import java.util.*

/**
 * @author Kyle Escobar
 */

class World(override val engine: Engine) : io.rsbox.api.World {

    val players = LivingEntityList(arrayOfNulls<Player>(2048))

    var currentTick = 0

    val random: Random = SecureRandom()

    fun init() {

    }

    fun pulse() {
        currentTick++
    }

    fun register(player: Player): Boolean {
        val registered = players.add(player)
        if(registered) {
            player.lastIndex = player.index
            return true
        }
        return false
    }
}