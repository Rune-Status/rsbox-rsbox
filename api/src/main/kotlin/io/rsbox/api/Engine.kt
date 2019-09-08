package io.rsbox.api

import io.rsbox.api.plugin.PluginManager
import net.runelite.cache.fs.Store

/**
 * Represents the game engine.
 *
 * @author Kyle Escobar
 */
interface Engine {

    /**
     * The current revision the server is running.
     */
    var revision: Int

    /**
     * The cache data loaded into a runelite store.
     */
    var cacheStore: Store

    /**
     * Initiates a startup of the game engine.
     */
    fun start()

    /**
     * Initiates a shutdown of the game engine.
     */
    fun stop()

    /**
     * Gets the loaded plugin manager.
     */
    val pluginManager: PluginManager
}