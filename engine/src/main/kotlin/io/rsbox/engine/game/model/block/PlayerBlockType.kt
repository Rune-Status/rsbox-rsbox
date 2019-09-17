package io.rsbox.engine.game.model.block

/**
 * @author Tom <rspsmods@gmail.com>
 */
enum class PlayerBlockType(val bit: Int) {

    APPEARANCE(0x1),

    ANIMATION(0x80),

    GFX(0x200),

    PUBLIC_CHAT(0x10),

    FACE_TILE(0x4),

    FACE_ENTITY(0x2),

    MOVEMENT(0x1000),

    CONTEXT_MENU(0x100),

    FORCE_MOVEMENT(0x400),

    HITMARK(0x40),

    FORCE_CHAT(0x20);

    companion object {

        const val mask: Int = 0x8

        const val opcode: Int = 79

        val values = enumValues<PlayerBlockType>()

    }
}