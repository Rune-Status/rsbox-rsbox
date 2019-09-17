package io.rsbox.engine.net.pregame.handshake

/**
 * @author Kyle Escobar
 */

enum class HandshakeType(val opcode: Int) {

    JS5(15),
    LOGIN(14);

}