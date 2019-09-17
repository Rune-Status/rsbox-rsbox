package io.rsbox.engine.task.impl

import io.rsbox.engine.game.model.World
import io.rsbox.engine.task.AsyncTask
import io.rsbox.engine.task.GameTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Phaser

/**
 * @author Kyle Escobar
 */

class PlayerCycleTask(private val executor: ExecutorService, private val world: World) : GameTask {

    private val phaser = Phaser(1)

    override fun execute() {
        val players = world.players
        val playerCount = players.count()

        phaser.bulkRegister(playerCount)
        players.forEach { player ->
            executor.execute {
                AsyncTask.run(phaser) {
                    player.prePulse()
                }
            }
        }
        phaser.arriveAndAwaitAdvance()

        phaser.bulkRegister(playerCount)
        players.forEach { player ->
            executor.execute {
                AsyncTask.run(phaser) {
                    player.pulse()
                }
            }
        }
        phaser.arriveAndAwaitAdvance()

        phaser.bulkRegister(playerCount)
        players.forEach { player ->
            executor.execute {
                AsyncTask.run(phaser) {
                    player.postPulse()
                }
            }
        }
        phaser.arriveAndAwaitAdvance()
    }
}