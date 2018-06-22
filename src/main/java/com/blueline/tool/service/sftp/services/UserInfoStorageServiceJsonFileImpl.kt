package com.blueline.tool.service.sftp.services

import com.blueline.tool.service.sftp.domain.CreateUserRequest
import com.blueline.tool.service.sftp.domain.UserInfo
import com.blueline.tool.service.sftp.runner.LoadUserRunner
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class UserInfoStorageServiceJsonFileImpl : UserInfoStorageService {


    @Value("\${sftp.user.config.path:./users/}")
    private var autoLoadDataPath: String = "./users/"

    @Autowired
    private lateinit var service: ServerService

    override fun loadUserInfo() {

        val autoLoadData = File(autoLoadDataPath)
        if (autoLoadData.exists()) {
            if (autoLoadData.isFile) {
                logger.warn("The specified user information directory is not a folder:{}", autoLoadDataPath)
            } else {
                val objectMapper = ObjectMapper()
                for (file in autoLoadData.listFiles()!!) {
                    if (file.isFile) {
                        try {
                            service.add(objectMapper.readValue<CreateUserRequest>(file, CreateUserRequest::class.java))?.let {
                                logger.info("Loaded user information successfully {}@{}", it.userName, it.rootPath)
                            }
                        } catch (e: IOException) {
                            logger.warn("Not a valid user information file : {}", file)
                        }

                    }
                }
            }
        } else {
            try {
                logger.warn("There is no user information to load :{}", autoLoadData.canonicalPath)
            } catch (e: IOException) {
                logger.warn("The specified user information directory does not exist :{}", autoLoadDataPath)
            }
        }
        service.start()
        logger.info("Sftp service info $service")
    }

    override fun saveUserInfo(userInfo: UserInfo) {
        val autoLoadData = File(autoLoadDataPath)
        autoLoadData.mkdirs()
        try {
            val userInfoFile = File("${autoLoadData.canonicalPath}/${userInfo.userName}")
            if (userInfoFile.exists()) {
                userInfoFile.delete()
            }
            val objectMapper = ObjectMapper()
            objectMapper.writeValue(userInfoFile, userInfo)
            service.add(userInfo)
        } catch (e: IOException) {
            logger.warn("The specified user information directory does not exist :{}", autoLoadDataPath)
        }

    }

    override fun deleteUserInfo(userInfo: UserInfo) {
        val autoLoadData = File(autoLoadDataPath)
        try {
            val userInfoFile = File("${autoLoadData.canonicalPath}/${userInfo.userName}")
            userInfoFile.delete()
            service.delete(userInfo.userName)
        } catch (e: IOException) {
            logger.warn("The specified user information directory does not exist :{}", autoLoadDataPath)
        }

    }

    companion object {

        private val logger = LoggerFactory.getLogger(LoadUserRunner::class.java)
    }
}
