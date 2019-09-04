package io.rsbox.game

/**
 * @author Kyle Escobar
 */

object Game {

    private val listenerSet = ListenerSet()

    fun onStart() {
        listenerSet.register().listen()
    }

    fun onStop() {

    }
}