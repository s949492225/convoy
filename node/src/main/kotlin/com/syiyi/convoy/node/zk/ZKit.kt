package com.syiyi.convoy.node.zk

import com.syiyi.convoy.node.config.AppConfig
import org.I0Itec.zkclient.ZkClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ZKit {

    @Autowired
    private lateinit var zkClient: ZkClient

    @Autowired
    private lateinit var appConfig: AppConfig

    fun createRootNode(){
        val exits=zkClient.exists(appConfig.zkRoot)
        if (exits){
            return
        }
        zkClient.createPersistent(appConfig.zkRoot)
    }

    fun createNode(path:String){
        zkClient.createEphemeral(path)
    }
}