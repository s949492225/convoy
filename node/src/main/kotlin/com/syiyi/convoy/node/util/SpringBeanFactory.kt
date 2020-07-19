package com.syiyi.convoy.node.util

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
object SpringBeanFactory : ApplicationContextAware {
    private lateinit var context: ApplicationContext

    fun <T> getBean(clazz: Class<T>): T {
        return context.getBean(clazz)
    }


    fun <T> getBean(name: String, clazz: Class<T>): T {
        return context.getBean(name, clazz)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}