package com.blueline.tool.service.sftp.runner


import com.blueline.tool.service.sftp.services.UserInfoStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(value = 1)
class LoadUserRunner : ApplicationRunner {

    @Autowired
    internal var service: UserInfoStorageService? = null

    override fun run(applicationArguments: ApplicationArguments) {
        service!!.loadUserInfo()

    }
}
