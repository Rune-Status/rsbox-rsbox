package io.rsbox.net.exception

import java.lang.RuntimeException

/**
 * @author Kyle Escobar
 */

class IllegalOpcodeException(override val message: String) : RuntimeException(message) {
}