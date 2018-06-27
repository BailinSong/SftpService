package com.blueline.tool.service.sftp.domain

open class UserInfo {
    var userName: String = ""
    var passWord: String = ""
    var rootPath: String = ""
        get() {
          return  if(field.isBlank()){
                "/$userName"
            }else{

                field
            }
        }
    var enable=false
    constructor()

    constructor(userName: String, passWord: String, rootPath: String,enable:Boolean=false) {
        this.userName = userName
        this.passWord = passWord
        this.rootPath = rootPath
        this.enable=enable
    }
}