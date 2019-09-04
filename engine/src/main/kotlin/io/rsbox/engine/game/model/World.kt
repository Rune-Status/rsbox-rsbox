package io.rsbox.engine.game.model

import io.rsbox.api.event.Event
import io.rsbox.api.event.impl.WorldLoadEvent
import io.rsbox.api.event.impl.WorldPostLoadEvent
import io.rsbox.api.event.impl.WorldPreLoadEvent
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

    private var currentTick = 0
    private val random: Random = SecureRandom()

    fun preLoad() {
        Event.trigger(WorldPreLoadEvent(this)) {}
    }

    fun load() {
        Event.trigger(WorldLoadEvent(this)) {}
    }

    fun postLoad() {
        Event.trigger(WorldPostLoadEvent(this)) {}
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