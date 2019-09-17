package io.rsbox.api.interf

/**
 * @author Kyle Escobar
 */

interface InterfaceManager {

    var displayMode: DisplayMode

    fun openGameScreen(displayMode: DisplayMode)

    fun openInterface(interfaceId: Int, type: InterfaceType, fullscreen: Boolean = false)

    fun openInterface(type: InterfaceType, autoClose: Boolean = false)

    fun openInterface(parent: Int, child: Int, interfaceId: Int = 0, type: Int, isModal: Boolean = false)

    fun openInterface(gameInterface: GameInterface)
}