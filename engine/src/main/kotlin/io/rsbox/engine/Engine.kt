package io.rsbox.engine

import com.google.common.base.Stopwatch
import io.rsbox.api.RSBox
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.game.model.World
import io.rsbox.engine.net.NetworkServer
import io.rsbox.engine.service.ServiceManager
import io.rsbox.engine.system.rsa.RSA
import mu.KLogging
import net.runelite.cache.fs.Store
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class Engine : io.rsbox.api.Engine {

    override var revision = -1
    var rsa: RSA = RSA()
    override lateinit var cacheStore: Store
    lateinit var networkServer: NetworkServer

    lateinit var world: World

    override fun start() {
        // Load server revision via server.yml
        revision = Conf.SERVER[ServerSpec.revision]

        logger.info("Server engine running revision [{}].", revision)

        this.loadCache()

        rsa.load()

        logger.info("Loading game world.")
        world = World(this)
        world.init()

        logger.info("Loading engine services.")
        ServiceManager.init(world)

        // Update API hooks
        RSBox.engine = this
        RSBox.world = world

        networkServer = NetworkServer(this)
        networkServer.start()
    }

    override fun stop() {
        networkServer.shutdown()
    }

    private fun loadCache() {
        val stopwatch = Stopwatch.createStarted()
        cacheStore = Store(File(PathConstants.CACHE_PATH))
        cacheStore.load()
        stopwatch.stop()

        if(cacheStore.indexes.size != 0) {
            logger.info("Loaded cache from data folder in {}ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS))
        }
    }

    companion object : KLogging()
}