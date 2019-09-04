package io.rsbox.api.event

/**
 * @author Kyle Escobar
 */

interface Cancellable {

    @Suppress("PropertyName")
    var _cancelled: Boolean

    fun cancel()

    fun isCancelled(): Boolean = _cancelled

}