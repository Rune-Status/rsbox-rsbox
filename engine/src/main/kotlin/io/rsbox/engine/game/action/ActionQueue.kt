package io.rsbox.engine.game.action

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import java.util.*
import kotlin.coroutines.createCoroutine

/**
 * @author Kyle Escobar
 */

class ActionQueue(private val dispatcher: CoroutineDispatcher) {

    private val queue = LinkedList<Action>()

    val size: Int get() = queue.size

    internal fun cycle() {

    }

    fun queue(ctx: Any, execution: ActionExecution, logic: Action.(CoroutineScope) -> Unit) {
        val action = Action(ctx, execution)
        val suspendingLogic = suspend { logic(action, CoroutineScope(dispatcher)) }

        action.coroutine = suspendingLogic.createCoroutine(completion = action)

        if(execution == ActionExecution.INSTANT) {
            cancelActions()
        }

        queue.addFirst(action)
    }

    fun cancelActions() {
        queue.forEach { it.cancel() }
        queue.clear()
    }

}