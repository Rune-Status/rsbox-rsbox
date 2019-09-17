package io.rsbox.engine.game.model

import io.rsbox.engine.Engine
import io.rsbox.engine.game.model.entity.LivingEntityList
import io.rsbox.engine.game.model.entity.Player
import kotlinx.coroutines.CoroutineDispatcher
import java.security.SecureRandom
import java.util.*

/**
 * @author Kyle Escobar
 */

class World(override val engine: Engine) : io.rsbox.api.World {

    val players = LivingEntityList(arrayOfNulls<Player>(2048))

    private var currentTick = 0
    private val random: Random = SecureRandom()

    internal lateinit var dispatcher: CoroutineDispatcher

    fun preLoad() {
    }

    fun load() {
    }

    fun postLoad() {
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