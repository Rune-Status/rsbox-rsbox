package io.rsbox.game

import io.rsbox.game.listeners.PlayerInitListener

/**
 * @author Kyle Escobar
 */

class ListenerSet {

    private val listeners = mutableListOf<GameListener>()

    fun register(): ListenerSet {
        listeners.add(PlayerInitListener())
        return this
    }

    fun listen() {
        listeners.forEach { listener ->
            listener.execute()
        }
    }
}