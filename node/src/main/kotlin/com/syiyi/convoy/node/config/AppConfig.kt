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

    @Value("\${app.zk.connect.timeout}")
    var zkConnectTimeout = 0
}