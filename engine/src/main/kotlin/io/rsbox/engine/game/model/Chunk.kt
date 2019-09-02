package io.rsbox.engine.game.model

/**
 * @author Kyle Escobar
 */

class Chunk {

    companion object {
        /**
         * The size of a chunk, in tiles.
         */
        const val CHUNK_SIZE = 8

        /**
         * The amount of chunks in a region.
         */
        const val CHUNKS_PER_REGION = 13

        /**
         * The amount of [Chunk]s that can be viewed at a time by a player.
         */
        const val CHUNK_VIEW_RADIUS = 3

        /**
         * The size of a region, in tiles.
         */
        const val REGION_SIZE = CHUNK_SIZE * CHUNK_SIZE

        /**
         * The size of the viewport a [gg.rsmod.game.model.entity.Player] can
         * 'see' at a time, in tiles.
         */
        const val MAX_VIEWPORT = CHUNK_SIZE * CHUNKS_PER_REGION
    }
}