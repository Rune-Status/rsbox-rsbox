package io.rsbox.engine.service.impl.login

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.service.Service
import mu.KLogging
import java.util.concurrent.Executors

/**
 * @author Kyle Escobar
 */

class LoginService : Service {

    private val loginThreads = Conf.SERVER[ServerSpec.game_login_threads]

    private val executor = Executors.newFixedThreadPool(loginThreads,
        ThreadFactoryBuilder()
            .setNameFormat("LOGIN-QUEUE")
            .setUncaughtExceptionHandler { t, e -> logger.error("Error ocurred in login queue thread $t.", e) }
            .build()
    )

    override fun start() {
        for(i in 0 until loginThreads) {
            executor.execute(LoginProcessor())
        }

        logger.info("Started login queue processor on {} threads.", loginThreads)
    }

    override fun stop() {
        executor.shutdownNow()
        logger.info("Stopped login queue processor on {} threads.", loginThreads)
    }

    companion object : KLogging()
}