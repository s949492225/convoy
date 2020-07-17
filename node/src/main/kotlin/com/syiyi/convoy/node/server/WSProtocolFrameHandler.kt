package com.syiyi.convoy.node.server

import com.syiyi.convoy.common.protocol.MessageProto
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.time.LocalDateTime
import java.util.*

class WSProtocolFrameHandler : SimpleChannelInboundHandler<MessageProto.Message>() {


    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: MessageProto.Message?) {
        if (msg != null) {
            val recContent = msg.content
            println("二进制协议服务端接受到的消息:$recContent")
            // 返回消息给客户端
            val replyMsg = MessageProto.Message.newBuilder().apply {
                id = UUID.randomUUID().toString().replace("-", "")
                content = "服务器时间: " + LocalDateTime.now() + "  ： " + recContent
                type = "text"
            }.build()
            ctx?.channel()?.writeAndFlush(replyMsg)
        }
    }

    /**
     * 客户端连接的时候触发
     */
    override fun handlerAdded(ctx: ChannelHandlerContext) { // LongText() 唯一的  ShortText() 不唯一
        println("二进制协议add：" + ctx.channel().id().asLongText())
        println("二进制协议add：" + ctx.channel().id().asShortText())
    }

    /**
     * 客户端断开连接的时候触发
     */
    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        println("二进制协议Removed：" + ctx.channel().id().asLongText())
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        println("二进制协议异常发生了...")
        ctx.close()
    }


}