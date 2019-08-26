package io.rsbox.net.exceptions

import java.lang.RuntimeException

/**
 * @author Kyle Escobar
 */

class ChannelClosedException(override val message: String) : RuntimeException(message)