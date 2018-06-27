package com.blueline.tool.service.sftp.controllers

import com.blueline.tool.service.sftp.domain.CreateUserRequest
import com.blueline.tool.service.sftp.services.ServerService
import com.blueline.tool.service.sftp.services.UserInfoStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/users")
class ServerController @Autowired
constructor(private val service: ServerService) {

    private var logger = LoggerFactory.getLogger(ServerController::class.java)
    private var configLogger = LoggerFactory.getLogger("Config")

    @Autowired
    private var storageService: UserInfoStorageService? = null

    @RequestMapping(method = [RequestMethod.POST])
    fun addUser(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<*> {
        var response: ResponseEntity<*>
        try {
            createUserRequest.userName.isBlank().apply {
                if(this){
                    throw RuntimeException("Invalid user information")
                }
            }
            val definition = service.add(createUserRequest)
            storageService!!.saveUserInfo(definition!!)
            response = ResponseEntity(definition, HttpStatus.CREATED)
            configLogger.info("{} - {}", "CREATED", createUserRequest)
        } catch (e: Exception) {
            response = ResponseEntity(createUserRequest, HttpStatus.UNPROCESSABLE_ENTITY)
            logger.warn("{}:{}", e.message, createUserRequest)
        }

        return response
    }

    @RequestMapping(method = [RequestMethod.GET], value = ["/{userName}"], produces = ["application/json"])
    fun getUser(@PathVariable("userName") userName: String): ResponseEntity<*> {
        return ResponseEntity(service.get(userName), HttpStatus.OK)
    }

    @RequestMapping(method = [RequestMethod.GET], produces = ["application/json"])
    fun listUsers(): ResponseEntity<*> {
        return ResponseEntity(service.list(), HttpStatus.OK)
    }

    @RequestMapping(method = [(RequestMethod.DELETE)], value = ["/{userName}"])
    fun deleteUser(@PathVariable("userName") userName: String): ResponseEntity<*> {
        val proxyDefinition = service.get(userName)

        storageService!!.deleteUserInfo(proxyDefinition!!)
        configLogger.info("{} - {}", "DELETE", proxyDefinition)
        return ResponseEntity(proxyDefinition, HttpStatus.OK)
    }

    @RequestMapping(method = [(RequestMethod.PATCH)], value = ["/{userName}"])
    fun patchUser(@PathVariable("userName") userName: String,@RequestBody data:Map<String, Object>): ResponseEntity<*> {
        val proxyDefinition = service.get(userName)
        val enable=data["enable"].toString().toBoolean()
        proxyDefinition!!.enable=enable
        storageService!!.saveUserInfo(proxyDefinition)
        configLogger.info("{} - {}", "PATCH", proxyDefinition)
        return ResponseEntity(proxyDefinition, HttpStatus.OK)
    }
}
