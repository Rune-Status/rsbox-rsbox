package io.rsbox.engine.net.pipeline

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import io.rsbox.engine.net.NetworkServer
import java.util.concurrent.Executors

/**
 * @author Kyle Escobar
 */

@ChannelHandler.Sharable
class ClientChannelInitializer(private val networkServer: NetworkServer) : ChannelInitializer<SocketChannel>() {

    private val globalTrafficThrottler = GlobalTrafficShapingHandler(Executors.newSingleThreadScheduledExecutor(),
        0,
        0,
        1000)

    override fun initChannel(ch: SocketChannel) {

        val channelTrafficThrottler = ChannelTrafficShapingHandler(0, 1024 * 1024, 1000)

        val timeout = IdleStateHandler(0, 30,1000)

        val p = ch.pipeline()

        p.addLast("global_traffic", globalTrafficThrottler)
        p.addLast("channel_traffic", channelTrafficThrottler)
        p.addLast("timeout", timeout)
        p.addLast("handler", ClientChannelHandler(networkServer))
    }

}