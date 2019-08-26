package io.rsbox.server.net.message.js5

import io.rsbox.server.net.Message

/**
 * @author Kyle Escobar
 */

data class JS5Request(
    val index: Int = -1,
    val archive: Int = -1,
    val priority: Boolean = false,
    val ignore: Boolean = false
) : Message