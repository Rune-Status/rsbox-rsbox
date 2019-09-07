package io.rsbox.server

import com.google.common.base.Stopwatch
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.Engine
import mu.KLogging
import java.util.concurrent.TimeUnit


/**
 * @author Kyle Escobar
 */

class Server {

    private lateinit var engine: Engine

    fun start() {
        logger.info { "Server is starting up..." }

        // Check if installed
        if(!Install.checkSetup()) {
            logger.error { "RSBox has not been installed yet. Please run the [server:install] gradle task or add --install CLI flag before starting the server." }
            System.exit(-1)
        }

        logger.info { "Server installation check passed." }

        this.loadConfigs()

        this.loadEngine()

        logger.info { "Server startup has completed." }
    }

    private fun loadConfigs() {
        val stopwatch = Stopwatch.createStarted()
        Conf.SERVER = Config { addSpec(ServerSpec) }.from.yaml.file(PathConstants.CONFIG_SERVER_PATH)
        Conf.SERVER.toYaml.toFile(PathConstants.CONFIG_SERVER_PATH)

        logger.info("Loaded configuration {}.", PathConstants.CONFIG_SERVER_PATH)
        stopwatch.stop()

        logger.info("Took {}ms to load the server.yml configuration.",stopwatch.elapsed(TimeUnit.MILLISECONDS))
    }

    private fun loadEngine() {
        logger.info { "Starting game engine." }
        engine = Engine()
        engine.start()
    }

    companion object : KLogging()
}