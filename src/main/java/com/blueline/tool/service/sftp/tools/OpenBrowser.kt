package com.blueline.tool.service.sftp.tools

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.awt.Desktop
import java.io.IOException
import java.net.URI

@Component
class OpenBrowser : CommandLineRunner {
    @Value("\${web.host:localhost}")
    private val host: String? = null
    @Value("\${server.port}")
    private val port: String? = null

    @Value("\${web.autoopen:false}")
    private val isOpen: Boolean = false

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (isOpen) {
            val url = "http://$host:$port"
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                try {
                    desktop.browse(URI(url))
                } catch (e: Exception) {
                }
            } else {
                val runtime = Runtime.getRuntime()
                try {
                    runtime.exec("xdg-open $url")
                } catch (e: IOException) {
                    try {
                        val cmd = "rundll32 url.dll FileProtocolHandler $url "//要打开的文件的路径
                        Runtime.getRuntime().exec(cmd)//创建一个子进程来执行以上命令，打开文件
                    } catch (e: IOException) {
                    }
                }

            }

        }
    }

}