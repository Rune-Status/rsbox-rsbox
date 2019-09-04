package io.rsbox.game.listeners

import io.rsbox.api.event.Event
import io.rsbox.api.event.impl.PlayerLoadEvent
import io.rsbox.api.interf.InterfaceType
import io.rsbox.game.GameListener

/**
 * @author Kyle Escobar
 */

class PlayerInitListener : GameListener {

    override fun execute() {
        this.setupPlayerGameScreen()
    }

    /**
     * When the player logs in, open the interfaces required for
     * game play. This includes the main pane where graphics are rendered.
     */
    private fun setupPlayerGameScreen() {
        Event.on(PlayerLoadEvent::class.java) {
            println("Fired")
            it.player.interfaces.openGameScreen(it.player.interfaces.displayMode)

            InterfaceType.values.filter { pane -> pane.interfaceId != -1 }.forEach { pane ->
                it.player.interfaces.openInterface(pane.interfaceId, pane)
            }
        }
    }

}