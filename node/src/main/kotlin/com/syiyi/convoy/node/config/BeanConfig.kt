package com.syiyi.convoy.node.config

import org.I0Itec.zkclient.ZkClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class BeanConfig {

    @Autowired
    private lateinit var appConfig: AppConfig

    @Bean
    fun buildZKClient(): ZkClient {
        return ZkClient(appConfig.zkAddr, appConfig.zkConnectTimeout)
    }
}