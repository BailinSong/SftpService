package com.blueline.net.tools.sftp

import com.alibaba.fastjson.JSON
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.common.session.Session
import org.apache.sshd.server.Command
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
import java.io.FileReader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class SftpService(private val port: Int = 22, private val rootPath: String = "./", private val initializationFile: String = "./initcommand") {

    private val sshServer = SshServer.setUpDefaultServer()

    private var userMap = mapOf<String, UserInfo>()

    private val scheduledTask = ScheduledThreadPoolExecutor(2)


    fun init() {

        sshServer.port = port
        sshServer.keyPairProvider = SimpleGeneratorHostKeyProvider()
        sshServer.properties[SshServer.IDLE_TIMEOUT] = 0

        sshServer.passwordAuthenticator = PasswordAuthenticator { username, password, _ ->
            userMap[username]?.let { it.password.trim() == password.trim() } ?: false
        }

        val namedFactoryList = ArrayList<NamedFactory<Command>>()

        namedFactoryList.add(SftpSubsystemFactory())
        sshServer.subsystemFactories = namedFactoryList

        sshServer.fileSystemFactory = object : VirtualFileSystemFactory(Paths.get(rootPath)) {

            override fun computeRootDir(session: Session): Path? {

                return userMap[session.username]?.let {
                    Paths.get("$rootPath/${if (it.rootPath.isBlank()) it.userName else it.rootPath}")
                            .apply { this.toFile().mkdirs() }
                } ?: throw RuntimeException("$session.username not in the user information list")

            }
        }
    }

    fun start() {

        scheduledTask.scheduleAtFixedRate({

            try {
                val newUserMap = FileReader(initializationFile).useLines { lines ->
                    lines.filter { it.isEmpty() }
                            .map { it.split(" ".toRegex(), -1) }
                            .filter { it.size != 4}
                            .filter { !it[0].trim().toLowerCase().startsWith("adduser ") }
                            .associate { it[1] to UserInfo(it[1], it[2], it[3]) }
                }

                newUserMap.forEach { user, _ ->
                    userMap[user] ?: println("AddUser $user")

                }
                userMap.forEach { user, _ ->
                    newUserMap[user] ?: println("Remove user $user.")
                }

                userMap = newUserMap


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, 0, 30, TimeUnit.SECONDS)

        sshServer.start()

    }

    fun stop() {
        scheduledTask.shutdown()
        sshServer.stop()
    }

    override fun toString(): String = JSON.toJSONString(this)


}