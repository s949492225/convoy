package com.syiyi.convoy.client

import com.syiyi.convoy.common.protocol.MessageProto
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.lang.Exception
import java.util.*

fun main() {
    val url = "ws:localhost:8989/ws"
    val client = OkHttpClient.Builder().build()
    val request = Request.Builder().url(url).build()
    var socket: WebSocket? = null
    client.newWebSocket(request, object : WebSocketListener() {

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            socket = null
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            socket = null
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            socket = null
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            println("接到服务器消息:$text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            try {
                val msg: MessageProto.Message = MessageProto.Message.parseFrom(bytes.toByteArray())
                println("接到服务器消息:${msg.content}")
            } catch (e: Exception) {
                println("二进制数据解析异常")
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            socket = webSocket
            println("socket已连接")
        }
    })

    var exit = false

    while (!exit) {
        val input = readLine()
        if (input.equals("exit")) {
            exit = true
            println("程序已退出")
        } else if (input == null || input.isEmpty()) {
            println("请输入有效的内容")
        } else {
            if (socket != null) {
                val inputMsg = MessageProto.Message.newBuilder().apply {
                    id = UUID.randomUUID().toString().replace("-", "")
                    content = input
                    type = "text"
                }.build()
                try {
                    inputMsg.toByteArray()?.let {
                        val success = socket!!.send(it.toByteString())
                    }
                } catch (e: Exception) {
                    println("二进制数据转换异常")
                }
            } else {
                println("socket已断开,请重新连接")
            }
        }
    }

}