package xyz.acrylicstyle.bcServerEditor

import net.blueberrymc.native_util.NativeUtil
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.config.ServerInfo
import java.lang.NumberFormatException
import java.net.InetSocketAddress

object Util {
    fun CommandSender.send(message: String) {
        sendMessage(*TextComponent.fromLegacyText(message))
    }

    fun createInetSocketAddress(address: String): InetSocketAddress {
        val split = address.split(":").toMutableList()
        if (split.size == 1) split.add("25565")
        if (split.size > 2) throw IllegalArgumentException("Invalid address")
        val port = try {
            Integer.parseInt(split[1], 10)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid port number")
        }
        if (port < 0 || port > 65535) throw IllegalArgumentException("Port number out of bounds")
        return InetSocketAddress.createUnresolved(split[0], port)
    }

    fun ServerInfo.setSocketAddress(address: InetSocketAddress) {
        try {
            val field = this::class.java.getDeclaredField("socketAddress")
            NativeUtil.set(field, this, address)
        } catch (ignore: ReflectiveOperationException) {}
    }

    fun ServerInfo.setRestricted(restricted: Boolean) {
        try {
            val field = this::class.java.getDeclaredField("restricted")
            NativeUtil.set(field, this, restricted)
        } catch (ignore: ReflectiveOperationException) {}
    }
}
