package com.blueline.tool.service.sftp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
open class SftpServiceApplication


fun main(args: Array<String>) {
    SpringApplication.run(SftpServiceApplication::class.java, *args)

}
