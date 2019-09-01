package io.rsbox.engine.net.game.model

/**
 * An enumeration which contains the different types of packets.
 *
 * @author Graham
 */
enum class PacketType {

    /**
     * A model where the length is known by both the client and server already.
     */
    FIXED,

    /**
     * A model with no header.
     */
    RAW,

    /**
     * A model where the length is sent to its destination with it as a byte.
     */
    VARIABLE_BYTE,

    /**
     * A model where the length is sent to its destination with it as a short.
     */
    VARIABLE_SHORT
}