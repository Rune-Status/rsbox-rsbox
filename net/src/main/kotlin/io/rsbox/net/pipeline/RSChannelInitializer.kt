package io.rsbox.net.pipeline

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import io.rsbox.net.NetworkServer
import java.util.concurrent.Executors

/**
 * @author Kyle Escobar
 */

class RSChannelInitializer(private val server: NetworkServer) : ChannelInitializer<SocketChannel>() {

    private val globalTrafficShaper = GlobalTrafficShapingHandler(
        Executors.newSingleThreadScheduledExecutor(),
        0,
        0,
        1000
    )

    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()

        p.addLast("global_traffic", globalTrafficShaper)
        p.addLast("channel_traffic", ChannelTrafficShapingHandler(0, 0, 1000))
        p.addLast("timeout", IdleStateHandler(0, 60, 0))
        p.addLast("handler", RSChannelHandler(server))
    }

}