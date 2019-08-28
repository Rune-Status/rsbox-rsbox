package io.rsbox.net.impl.js5

import io.rsbox.net.Message

/**
 * @author Kyle Escobar
 */

data class JS5Request(val index: Int, val archive: Int, val priority: Boolean) : Message