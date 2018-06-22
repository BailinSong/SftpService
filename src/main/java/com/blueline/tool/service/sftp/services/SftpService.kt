package com.blueline.tool.service.sftp.services

import com.alibaba.fastjson.JSON
import com.blueline.tool.service.sftp.domain.UserInfo
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.common.session.Session
import org.apache.sshd.server.Command
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class SftpService : ServerService {
    @Value("\${sftp.port:22}")
    private var sftpPort: Int = 22

    @Value("\${sftp.user.root.path:./}")
    private val sftpRootPath: String = "./"

    private val sshServer = SshServer.setUpDefaultServer()

    private val userMap = ConcurrentHashMap<String, UserInfo>()

    init {
        with(sshServer){
        port = sftpPort
        keyPairProvider = SimpleGeneratorHostKeyProvider()
        properties[SshServer.IDLE_TIMEOUT] = 0

        passwordAuthenticator = PasswordAuthenticator { username, password, _ ->
            userMap[username]?.let { it.passWord.trim() == password.trim() } ?: false
        }

        val namedFactoryList = ArrayList<NamedFactory<Command>>()

        namedFactoryList.add(SftpSubsystemFactory())
        subsystemFactories = namedFactoryList
        fileSystemFactory = object : VirtualFileSystemFactory(Paths.get(File(sftpRootPath).canonicalPath)) {

            override fun computeRootDir(session: Session): Path? {

                return userMap[session.username]?.let { user ->
                    when (user.rootPath[0]) {
                        '/' -> user.rootPath.substring(1)
                        '\\' -> user.rootPath.substring(1)
                        '@' -> "@${File(user.rootPath.substring(1)).canonicalPath}"
                        else -> user.rootPath
                    }.let {
                        when {
                            it.startsWith('@') -> Paths.get(it.substring(1))
                            else -> Paths.get("$defaultHomeDir/${if (user.rootPath.isBlank()) user.userName else it}")
                        }.apply {
                            this.toFile().mkdir()
                        }
                    }
                } ?: throw RuntimeException("$session.username not in the user information list")

            }
        }
        }
    }

    override fun start() {

        sshServer.start()

    }

    override fun stop() {

        sshServer.stop()
    }

    override fun add(user: UserInfo): UserInfo? {
        userMap.put(user.userName, user)
        return user
    }

    override fun get(userName: String): UserInfo? {
        return userMap[userName]
    }

    override fun delete(userName: String): UserInfo? {
        return userMap.remove(userName)
    }

    override fun list(): Set<UserInfo> {
        return userMap.map { (_, V) -> V }.toSet()
    }

    override fun toString(): String = """{"sftp":{"port":"$sftpPort","rootpath":"${File(sftpRootPath).canonicalPath}"}}""".trimMargin()


}