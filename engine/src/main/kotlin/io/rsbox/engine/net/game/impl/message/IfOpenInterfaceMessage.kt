package io.rsbox.engine.net.game.impl.message

import io.rsbox.engine.net.game.Message

/**
 * @author Kyle Escobar
 */

data class IfOpenInterfaceMessage(val parent: Int, val child: Int, val component: Int, val type: Int) : Message