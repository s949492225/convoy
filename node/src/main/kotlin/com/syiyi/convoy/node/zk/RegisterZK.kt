package com.syiyi.convoy.node.zk

import com.syiyi.convoy.node.config.AppConfig
import com.syiyi.convoy.node.util.SpringBeanFactory

class RegisterZK(private val ip: String, private val serverPort: Int, private val httpPort: Int) : Runnable {
    private val zKit: ZKit by lazy { SpringBeanFactory.getBean(ZKit::class.java) }

    private val appConfig: AppConfig by lazy { SpringBeanFactory.getBean(AppConfig::class.java) }

    override fun run() {
        zKit.createRootNode()
        if (appConfig.zkSwitch) {
            val path = "${appConfig.zkRoot}/ip-$ip:$serverPort:$httpPort"
            zKit.createNode(path)
            println("zookeeper success register")
        }
    }

}