package com.syiyi.convoy.node.server

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.stream.ChunkedWriteHandler

class WsChannelInitializer : ChannelInitializer<Channel>() {
    //定义通过处理
    override fun initChannel(ch: Channel) {
        ch.pipeline().apply {
            // websocket是基于http协议的，所以需要使用http编解码器
            addLast(HttpServerCodec()) // 对写大数据流的支持
                    .addLast(ChunkedWriteHandler()) // 对http消息的聚合，聚合成FullHttpRequest或FullHttpResponse
                    .addLast(HttpObjectAggregator(1024 * 64))//在Netty的编程中，几乎都会使用到这个handler
            addLast(WebSocketServerProtocolHandler("/ws"))
            // 自定义的处理器
            addLast(WSHandler())
        }
    }
}