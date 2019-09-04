package io.rsbox.engine.game.model.interf

import io.rsbox.api.interf.DisplayMode
import io.rsbox.api.interf.InterfaceType
import io.rsbox.api.interf.InterfaceUtils
import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.game.impl.message.IfOpenGameScreenMessage

/**
 * @author Kyle Escobar
 */

class InterfaceManager(private val player: Player) {

    private val interfaces = InterfaceSet()

    var displayMode: DisplayMode
        get() = interfaces.displayMode
        set(displayMode: DisplayMode) { interfaces.displayMode = displayMode }

    fun openGameScreen(displayMode: DisplayMode) {
        if(displayMode != this.displayMode) {
            interfaces.setVisible(
                parent = InterfaceUtils.getDisplayComponentId(this.displayMode),
                child = InterfaceUtils.getChildId(InterfaceType.MAIN_SCREEN, this.displayMode),
                visible = false
            )
        }


        val component = InterfaceUtils.getDisplayComponentId(displayMode)
        interfaces.setVisible(
            parent = InterfaceUtils.getDisplayComponentId(displayMode),
            child = 0,
            visible = true
        )

        player.write(IfOpenGameScreenMessage(component))
    }
}