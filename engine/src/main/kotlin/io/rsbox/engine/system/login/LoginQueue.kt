package io.rsbox.engine.system.login

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.net.pregame.login.LoginRequest
import io.rsbox.engine.service.login.LoginService
import mu.KLogging
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author Kyle Escobar
 */

class LoginQueue(val service: LoginService) {

    val queue = LinkedBlockingQueue<LoginRequest>()

    private val loginThreads = Conf.SERVER[ServerSpec.game_login_threads]
    private val executor = Executors.newFixedThreadPool(loginThreads,
        ThreadFactoryBuilder()
            .setNameFormat("login-queue")
            .setUncaughtExceptionHandler { t, e -> logger.error("Unable to process login request in thread $t.", e) }
            .build())

    fun startProcessor() {
        for(i in 0 until loginThreads) {
            executor.execute(LoginWorker(this))
        }

        logger.info("Login queue processor has been started. Login requests will be processed on $loginThreads threads.")
    }

    fun stopProcessor() {
        executor.shutdownNow()
        logger.info("Login queue processor has been stopped. No more login requests will be processed.")
    }

    companion object : KLogging()
}