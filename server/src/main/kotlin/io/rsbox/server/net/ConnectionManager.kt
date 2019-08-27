package io.rsbox.server.net

import io.netty.channel.Channel
import io.rsbox.server.net.session.Session

/**
 * @author Kyle Escobar
 */

/**
 * Defines a basic structure for any object which manages connections.
 */
interface ConnectionManager {

    /**
     * Creates a new session for a [Channel]. This session will be used for all network actions.
     * The session will be saved by the [ConnectionManager] in order to interact with the session
     * at any later date.
     *
     * @param channel The channel the session will be using.
     * @return The new session.
     */
    fun newSession(channel: Channel): Session

    /**
     * Called when a session becomes inactive because the underlying channel was closed.
     * All references to the session should be removed.
     *
     * @param session The session which has become inactive.
     */
    fun sessionInvalidated(session: Session)

    /**
     * Called to initiate a server shutdown.
     */
    fun shutdown()
}