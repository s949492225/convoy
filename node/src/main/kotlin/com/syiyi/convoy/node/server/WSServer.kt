package com.syiyi.convoy.node.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class WSServer {

    private val boss: EventLoopGroup = NioEventLoopGroup()
    private val work: EventLoopGroup = NioEventLoopGroup()

    @Value("\${ws.server.port}")
    private var nettyPort: Int = 0


    /**
     * 启动ws服务器
     */
    @PostConstruct
    fun start() {
        try {
            val bootstrap = ServerBootstrap().apply {
                group(boss, work)
                channel(NioServerSocketChannel::class.java)
                childOption(ChannelOption.SO_KEEPALIVE, true)
                childHandler(WsChannelInitializer())
            }
            bootstrap.bind(nettyPort).sync().apply {
                if (isSuccess) {
                    println("ws服务节点启动成功")
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @PreDestroy
    fun destroy() {
        boss.shutdownGracefully()
        work.shutdownGracefully()
    }
}