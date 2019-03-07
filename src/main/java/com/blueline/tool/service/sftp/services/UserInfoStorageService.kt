package com.blueline.tool.service.sftp.services

import com.blueline.tool.service.sftp.domain.UserInfo

interface UserInfoStorageService {
    fun loadUserInfo()
    fun saveUserInfo(userInfo: UserInfo)
    fun deleteUserInfo(userInfo: UserInfo)

}
