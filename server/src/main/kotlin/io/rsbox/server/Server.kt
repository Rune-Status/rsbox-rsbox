package io.rsbox.server

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml.toYaml
import io.rsbox.config.Conf
import io.rsbox.config.PathConstants
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.Engine
import io.rsbox.net.NetworkServer
import mu.KLogging

/**
 * @author Kyle Escobar
 */

class Server {

    lateinit var engine: Engine

    lateinit var networkServer: NetworkServer

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

        this.loadNetwork()

        logger.info { "Server startup has completed." }
    }

    private fun loadConfigs() {
        Conf.SERVER = Config { addSpec(ServerSpec) }.from.yaml.file(PathConstants.CONFIG_SERVER_PATH)
        Conf.SERVER.toYaml.toFile(PathConstants.CONFIG_SERVER_PATH)

        logger.info("Loaded configuration {}.", PathConstants.CONFIG_SERVER_PATH)
    }

    private fun loadEngine() {
        logger.info { "Starting game engine." }
        engine = Engine()
        engine.start()
    }

    private fun loadNetwork() {
        logger.info { "Starting networking." }
        networkServer = NetworkServer(engine)
        networkServer.start()
    }

    companion object : KLogging()
}