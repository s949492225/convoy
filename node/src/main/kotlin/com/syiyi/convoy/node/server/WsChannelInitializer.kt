package com.syiyi.convoy.node.server

import com.google.protobuf.MessageLite
import com.google.protobuf.MessageLiteOrBuilder
import com.syiyi.convoy.common.protocol.MessageProto
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled.wrappedBuffer
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.timeout.IdleStateHandler


class WsChannelInitializer : ChannelInitializer<Channel>() {
    //定义通过处理
    override fun initChannel(ch: Channel) {
        ch.pipeline().addLast(HttpServerCodec(), // websocket是基于http协议的，所以需要使用http编解码器
                ChunkedWriteHandler(),// 对http消息的聚合，聚合成FullHttpRequest或FullHttpResponse// 对写大数据流的支持
                HttpObjectAggregator(1024 * 64),//在Netty的编程中，几乎都会使用到这个handler
                IdleStateHandler(11, 0, 0),
                WebSocketServerProtocolHandler("/ws"),
                // google Protobuf 编解码
                // 协议包解码
                object : MessageToMessageDecoder<WebSocketFrame>() {
                    override fun decode(ctx: ChannelHandlerContext, frame: WebSocketFrame, out: MutableList<Any>) {
                        val buf = (frame as BinaryWebSocketFrame).content()
                        out.add(buf)
                        buf.retain()
                    }
                },
                // 协议包编码
                object : MessageToMessageDecoder<MessageLiteOrBuilder>() {
                    override fun decode(ctx: ChannelHandlerContext, msg: MessageLiteOrBuilder, out: MutableList<Any>) {
                        var result: ByteBuf? = null
                        if (msg is MessageLite) {
                            result = wrappedBuffer(msg.toByteArray())
                        }
                        if (msg is MessageLite.Builder) {
                            result = wrappedBuffer(msg.build().toByteArray())
                        }

                        val frame: WebSocketFrame = BinaryWebSocketFrame(result)
                        out.add(frame)
                    }
                },
                ProtobufDecoder(MessageProto.Message.getDefaultInstance()),
                WSTextFrameHandler(),//文本协议处理
                WSProtocolFrameHandler())//二进制协议处理
    }
}