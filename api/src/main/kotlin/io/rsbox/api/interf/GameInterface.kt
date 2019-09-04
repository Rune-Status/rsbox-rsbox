package io.rsbox.api.interf

import io.rsbox.api.entity.Player

/**
 * @author Kyle Escobar
 */

open class GameInterface {

    var parent: Int = -1

    var child: Int = -1

    var interfaceId: Int = -1

    var type: InterfaceType = InterfaceType.MAIN_SCREEN

    constructor(interfaceId: Int, type: InterfaceType = InterfaceType.MAIN_SCREEN) {
        this.interfaceId = interfaceId
        this.type = type
    }

    constructor(parent: Int, child: Int, type: InterfaceType = InterfaceType.MAIN_SCREEN) : this((parent shl 16) or child, type) {
        this.parent = parent
        this.child = child
    }

    fun open(player: Player) {
        player.interfaces.openInterface(interfaceId, type)
    }
}