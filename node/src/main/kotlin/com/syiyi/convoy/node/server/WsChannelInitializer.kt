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
import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.stream.ChunkedWriteHandler


class WsChannelInitializer : ChannelInitializer<Channel>() {
    //定义通过处理
    override fun initChannel(ch: Channel) {
        val pipeline = ch.pipeline()
        // HTTP请求的解码和编码
        // HTTP请求的解码和编码
        pipeline.addLast(HttpServerCodec())
        // 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
        // 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
        // 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
        // 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
        pipeline.addLast(HttpObjectAggregator(65536))
        // 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
        // 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
        pipeline.addLast(ChunkedWriteHandler())
        // WebSocket数据压缩
        // WebSocket数据压缩
        pipeline.addLast(WebSocketServerCompressionHandler())
        // 协议包长度限制
        // 协议包长度限制
        pipeline.addLast(WebSocketServerProtocolHandler("/ws", null, true))
        // 协议包解码
        // 协议包解码
        pipeline.addLast(object : MessageToMessageDecoder<WebSocketFrame>() {
            @Throws(Exception::class)
            override fun decode(ctx: ChannelHandlerContext, frame: WebSocketFrame, objs: MutableList<Any>) {
                val buf = (frame as BinaryWebSocketFrame).content()
                objs.add(buf)
                buf.retain()
            }
        })
        // 协议包编码
        // 协议包编码
        pipeline.addLast(object : MessageToMessageEncoder<MessageLiteOrBuilder?>() {
            @Throws(Exception::class)
            override fun encode(ctx: ChannelHandlerContext, msg: MessageLiteOrBuilder?, out: MutableList<Any>) {
                var result: ByteBuf? = null
                if (msg is MessageLite) {
                    result = wrappedBuffer(msg.toByteArray())
                }
                if (msg is MessageLite.Builder) {
                    result = wrappedBuffer(msg.build().toByteArray())
                }
                // ==== 上面代码片段是拷贝自TCP ProtobufEncoder 源码 ====
                // 然后下面再转成websocket二进制流，因为客户端不能直接解析protobuf编码生成的
                val frame: WebSocketFrame = BinaryWebSocketFrame(result)
                out.add(frame)
            }
        })

        // 协议包解码时指定Protobuf字节数实例化为CommonProtocol类型
        // 协议包解码时指定Protobuf字节数实例化为CommonProtocol类型
        pipeline.addLast(ProtobufDecoder(MessageProto.Message.getDefaultInstance()))

        // websocket定义了传递数据的6中frame类型
        // websocket定义了传递数据的6中frame类型
        pipeline.addLast(WSProtocolFrameHandler())
    }
}