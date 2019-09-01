package io.rsbox.engine.service.game

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.config.Conf
import io.rsbox.config.specs.ServerSpec
import io.rsbox.engine.game.model.World
import io.rsbox.engine.service.Service
import io.rsbox.engine.task.GameTask
import io.rsbox.engine.task.impl.PlayerPulseTask
import mu.KLogging
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Kyle Escobar
 */

class GameService(private val world: World) : Service {

    private val pulseInterval = Conf.SERVER[ServerSpec.game_pulse_interval]

    private val executor = Executors.newSingleThreadScheduledExecutor(
        ThreadFactoryBuilder()
            .setNameFormat("game-thread")
            .setUncaughtExceptionHandler { t, e -> logger.error("An error occured in game-thread $t.", e) }
            .build())

    private val tasks = mutableListOf<GameTask>()

    override fun start() {
        this.registerTasks()
        executor.scheduleAtFixedRate(this::pulse, 0, pulseInterval.toLong(), TimeUnit.MILLISECONDS)
        logger.info("Game service is now running.")
    }

    override fun stop() {
        executor.shutdownNow()
        logger.info("Game service has stopped.")
    }

    private fun pulse() {

        /**
         * Run the tasks
         */
        tasks.forEach { task ->
            task.execute()
        }

    }

    private fun registerTasks() {
        val maxThreads = Runtime.getRuntime().availableProcessors()
        val taskExecutor = Executors.newFixedThreadPool(maxThreads,
            ThreadFactoryBuilder()
                .setNameFormat("game-task-thread")
                .setUncaughtExceptionHandler { t, e -> logger.error("An error occurred in thread $t.", e) }
                .build())

        tasks.addAll(arrayOf(
            PlayerPulseTask(taskExecutor, world)
        ))

        logger.info("{} tasks will be processed every {}ms on {} threads.", tasks.size, pulseInterval, maxThreads)
    }

    companion object : KLogging()
}