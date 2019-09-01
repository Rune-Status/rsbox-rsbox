package io.rsbox.engine

import com.google.common.base.Stopwatch
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
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

class Engine {

    var revision = -1
    var rsa: RSA = RSA()
    lateinit var cacheStore: Store
    lateinit var networkServer: NetworkServer

    fun start() {
        // Load server revision via server.yml
        revision = Conf.SERVER[ServerSpec.revision]

        logger.info("Server engine running revision [{}].", revision)

        this.loadCache()

        rsa.load()

        logger.info("Loading engine services.")
        ServiceManager.init()

        networkServer = NetworkServer(this)
        networkServer.start()
    }

    fun stop() {
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