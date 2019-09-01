package io.rsbox.engine.net.pregame.js5

import io.rsbox.engine.net.Message

/**
 * @author Kyle Escobar
 */

data class JS5Request(val index: Int, val archive: Int, val priority: Boolean) : Message