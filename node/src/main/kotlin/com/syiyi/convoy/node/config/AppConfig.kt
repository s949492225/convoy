package com.syiyi.convoy.node.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppConfig {
    @Value("\${app.zk.root}")
    var zkRoot: String = ""

    @Value("\${app.zk.addr}")
    var zkAddr: String = ""

    @Value("\${app.zk.switch}")
    var zkSwitch = false

    @Value("\${cim.server.port}")
    var cimServerPort = 0

    @Value("\${cim.route.url}")
    var routeUrl: String = ""

    @Value("\${cim.heartbeat.time}")
    var heartBeatTime: Long = 0

    @Value("\${app.zk.connect.timeout}")
    var zkConnectTimeout = 0
}