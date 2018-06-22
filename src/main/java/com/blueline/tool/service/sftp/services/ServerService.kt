package com.blueline.tool.service.sftp.services

import com.blueline.tool.service.sftp.domain.UserInfo


open interface ServerService {
    fun add(user:UserInfo):UserInfo?
    fun start()
    fun stop()
    fun get(userName: String): UserInfo?
    fun list(): Set<UserInfo>
    fun delete(userName: String): UserInfo?
}
