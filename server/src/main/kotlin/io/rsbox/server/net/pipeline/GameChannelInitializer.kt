package io.rsbox.server.net.pipeline

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import io.rsbox.server.config.Conf
import io.rsbox.server.config.spec.ServerSpec
import io.rsbox.server.net.GameServer
import java.util.concurrent.Executors

/**
 * @author Kyle Escobar
 */

class GameChannelInitializer(val gameServer: GameServer) : ChannelInitializer<SocketChannel>() {

    private val globalTraffic = GlobalTrafficShapingHandler(
        Executors.newSingleThreadScheduledExecutor(),
        0, 0, 1000
    )

    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()

        val timeout = IdleStateHandler(0, Conf.SERVER[ServerSpec.network_idle_timeout], 0)

        p.addLast("global_traffic", globalTraffic)
        p.addLast("channel_traffic", ChannelTrafficShapingHandler(0, 1024 * 512, 1000))
        p.addLast("timeout", timeout)
        p.addLast("handler", GameChannelHandler(gameServer))
    }
}