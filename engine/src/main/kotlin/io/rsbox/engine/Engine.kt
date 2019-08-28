package io.rsbox.engine

import com.google.common.base.Stopwatch
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import mu.KLogging
import net.runelite.cache.fs.Store
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class Engine {

    fun start() {
        // Load server revision via server.yml
        REVISION = Conf.SERVER[ServerSpec.revision]

        logger.info("Server engine running revision [{}].", REVISION)

        this.loadCache()
    }

    fun stop() {

    }

    private fun loadCache() {
        val stopwatch = Stopwatch.createStarted()
        CACHE = Store(File(PathConstants.CACHE_PATH))
        CACHE.load()
        stopwatch.stop()

        if(CACHE.indexes.size != 0) {
            logger.info("Loaded cache from data folder in {}ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS))
        }
    }

    companion object : KLogging() {
        var REVISION: Int = -1
        lateinit var CACHE: Store
    }
}