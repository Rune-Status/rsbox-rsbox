package io.rsbox.net.impl.login

/**
 * @author Kyle Escobar
 */

enum class LoginType(val opcode: Int) {
    NORMAL(16),
    RECONNECTING(18);
}