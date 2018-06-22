package com.blueline.tool.service.sftp.domain

import com.alibaba.fastjson.JSON


class CreateUserRequest : UserInfo(){
    override fun toString(): String {
        return JSON.toJSONString(this)
    }
}
