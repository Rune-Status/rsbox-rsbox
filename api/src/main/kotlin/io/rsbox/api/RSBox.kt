package io.rsbox.api

import kotlinx.coroutines.CoroutineDispatcher

/**
 * The static object class holding references to parts of the game
 * engine to be called within the API.
 *
 * @author Kyle Escobar
 */
object RSBox {

    /**
     * Reference to the game engine.
     */
    lateinit var engine: Engine

    /**
     * Reference to the game world.
     */
    lateinit var world: World

}