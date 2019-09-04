package io.rsbox.api.interf

/**
 * @author Kyle Escobar
 */

object InterfaceUtils {

    fun getDisplayComponentId(displayMode: DisplayMode) = when (displayMode) {
        DisplayMode.FIXED -> 548
        DisplayMode.RESIZABLE_NORMAL -> 161
        DisplayMode.RESIZABLE_LIST -> 164
        DisplayMode.FULLSCREEN -> 165
        else -> throw RuntimeException("Unhandled display mode.")
    }

    fun getChildId(pane: InterfaceType, displayMode: DisplayMode): Int = when (displayMode) {
        DisplayMode.FIXED -> pane.fixedChildId
        DisplayMode.RESIZABLE_NORMAL -> pane.resizeChildId
        DisplayMode.RESIZABLE_LIST -> pane.resizeListChildId
        DisplayMode.FULLSCREEN -> pane.fullscreenChildId
        else -> throw RuntimeException("Unhandled display mode.")
    }

}