package com.syiyi.convoy.node.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import java.time.LocalDateTime

class WSTextFrameHandler : SimpleChannelInboundHandler<TextWebSocketFrame>() {
    @Throws(Exception::class)
    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, textWebSocketFrame: TextWebSocketFrame) { // 打印接收到的消息
        println("服务端接受到的消息:" + textWebSocketFrame.text())
        // 返回消息给客户端
        channelHandlerContext.writeAndFlush(TextWebSocketFrame("服务器时间: " + LocalDateTime.now() + "  ： " + textWebSocketFrame.text()))
    }

    /**
     * 客户端连接的时候触发
     */
    override fun handlerAdded(ctx: ChannelHandlerContext) { // LongText() 唯一的  ShortText() 不唯一
        println("handlerAdded：" + ctx.channel().id().asLongText())
        println("handlerAdded：" + ctx.channel().id().asShortText())
    }

    /**
     * 客户端断开连接的时候触发
     */
    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        println("handlerRemoved：" + ctx.channel().id().asLongText())
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        println("异常发生了...")
        ctx.close()
    }
}