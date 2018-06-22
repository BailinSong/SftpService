package com.blueline.tool.service.sftp.domain

class UserDefinition(userName: String, password: String, rootPath: String) : UserInfo(userName, password, rootPath) {
    //	private String alias;
    val startedTime: Long

    init {
        startedTime = System.currentTimeMillis()
    }

    override fun equals(o: Any?): Boolean {
        return when(o){
            is UserDefinition->this.userName==o.userName
            else->false
        }
    }

    override fun hashCode(): Int {
        return userName.hashCode()
    }
}
