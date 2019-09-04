package io.rsbox.engine.game.model.interf

import io.rsbox.api.interf.DisplayMode
import io.rsbox.api.interf.GameInterface
import io.rsbox.api.interf.InterfaceType
import io.rsbox.engine.game.model.entity.Player
import io.rsbox.engine.net.game.impl.message.IfOpenGameScreenMessage
import io.rsbox.engine.net.game.impl.message.IfOpenInterfaceMessage

/**
 * @author Kyle Escobar
 */

class InterfaceManager(private val player: Player) : io.rsbox.api.interf.InterfaceManager {

    private val interfaces = InterfaceSet()

    override var displayMode: DisplayMode
        get() = interfaces.displayMode
        set(displayMode: DisplayMode) { interfaces.displayMode = displayMode }

    override fun openGameScreen(displayMode: DisplayMode) {
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

    override fun openInterface(interfaceId: Int, type: InterfaceType, fullscreen: Boolean) {
        val displayMode = if(!fullscreen || type.fullscreenChildId == -1) this.displayMode else DisplayMode.FULLSCREEN
        val child = InterfaceUtils.getChildId(type, displayMode)
        val parent = InterfaceUtils.getDisplayComponentId(displayMode)
        if(displayMode == DisplayMode.FULLSCREEN) {
            openGameScreen(displayMode)
        }
        openInterface(parent, child, interfaceId, if(type.clickThrough) 1 else 0, isModal = type == InterfaceType.MAIN_SCREEN)
    }

    override fun openInterface(type: InterfaceType, autoClose: Boolean) {
        val displayMode = if(!autoClose || type.fullscreenChildId == -1) this.displayMode else DisplayMode.FULLSCREEN
        val child = InterfaceUtils.getChildId(type, displayMode)
        val parent = InterfaceUtils.getDisplayComponentId(displayMode)
        if(displayMode == DisplayMode.FULLSCREEN) {
            openGameScreen(displayMode)
        }
        openInterface(parent, child, type.interfaceId, if(type.clickThrough) 1 else 0, isModal = type == InterfaceType.MAIN_SCREEN)
    }

    override fun openInterface(parent: Int, child: Int, interfaceId: Int, type: Int, isModal: Boolean) {
        if(isModal) {
            interfaces.openModal(parent, child, interfaceId)
        } else {
            interfaces.open(parent, child, interfaceId)
        }
        player.write(IfOpenInterfaceMessage(parent, child, interfaceId, type))
    }

    override fun openInterface(gameInterface: GameInterface) {
        gameInterface.open(player)
    }
}