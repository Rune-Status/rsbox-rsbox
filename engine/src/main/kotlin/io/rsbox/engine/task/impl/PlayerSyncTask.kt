package io.rsbox.engine.task.impl

import io.rsbox.api.RSBox
import io.rsbox.engine.game.model.World
import io.rsbox.engine.game.model.entity.LivingEntity
import io.rsbox.engine.sync.SyncTask
import io.rsbox.engine.sync.task.PlayerPostSync
import io.rsbox.engine.sync.task.PlayerPreSync
import io.rsbox.engine.sync.task.PlayerSync
import io.rsbox.engine.task.GameTask
import mu.KLogging
import java.util.concurrent.ExecutorService
import java.util.concurrent.Phaser

/**
 * @author Kyle Escobar
 */

class PlayerSyncTask(private val executor: ExecutorService) : GameTask {

    private val phaser = Phaser(1)
    private val world = RSBox.world as World


    override fun execute() {
        val players = world.players
        val playerCount = players.count()

        /**
         * Player PRE Sync Task
         */
        phaser.bulkRegister(playerCount)
        players.forEach { player ->
            submit(phaser, executor, player, PlayerPreSync)
        }
        phaser.arriveAndAwaitAdvance()

        /**
         * Player Sync Task
         */
        phaser.bulkRegister(playerCount)
        players.forEach { player ->
            submit(phaser, executor, player, PlayerSync)
        }
        phaser.arriveAndAwaitAdvance()

        /**
         * Player POST Sync Task
         */
        phaser.bulkRegister(playerCount)
        players.forEach { player ->
            submit(phaser, executor, player, PlayerPostSync)
        }
        phaser.arriveAndAwaitAdvance()
    }

    private fun <T : LivingEntity> submit(phaser: Phaser, executor: ExecutorService, livingEntity: T, task: SyncTask<T>) {
        executor.execute {
            try {
                task.execute(livingEntity)
            } catch(e : Exception) {
                logger.error("An error occurred in taks ${this::class.java} for entity $livingEntity.")
            } finally {
                phaser.arriveAndDeregister()
            }
        }
    }

    companion object : KLogging()
}