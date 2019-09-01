package io.rsbox.engine.net.pregame.js5

/**
 * @author Kyle Escobar
 */

enum class JS5RequestType(val opcode: Int) {

    ARCHIVE_PRIORITY(0),
    ARCHIVE_NORMAL(1),
    CLIENT_STATUS_1(2),
    CLIENT_STATUS_2(3),
    CLIENT_STATUS_3(6)

}