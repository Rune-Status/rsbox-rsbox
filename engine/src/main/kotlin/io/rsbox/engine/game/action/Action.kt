package io.rsbox.engine.game.action

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author Kyle Escobar
 */

data class Action(val ctx: Any, val execution: ActionExecution) : Continuation<Unit> {

    lateinit var coroutine: Continuation<Unit>

    var invoked = false

    var returnValue: Any? = null

    var cancelLogic: ((Action).() -> Unit)? = null

    var nextStep: Any? = null

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: Result<Unit>) {
        nextStep = null
    }

    internal fun cycle() {
        val next = nextStep ?: return
    }

    fun cancel() {
        nextStep = null
        returnValue = null
        cancelLogic?.invoke(this)
    }

    fun isWaiting(): Boolean = nextStep != null

}