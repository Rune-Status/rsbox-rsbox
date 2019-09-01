package io.rsbox.engine.net.pregame.login

/**
 * @author Kyle Escobar
 */

enum class LoginRequestType(val opcode: Int) {

    NORMAL(16),
    RECONNECT(18);

}