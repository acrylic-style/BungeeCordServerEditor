package xyz.acrylicstyle.bcServerEditor

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor
import xyz.acrylicstyle.bcServerEditor.Util.createInetSocketAddress
import xyz.acrylicstyle.bcServerEditor.Util.send
import xyz.acrylicstyle.bcServerEditor.Util.setRestricted
import xyz.acrylicstyle.bcServerEditor.Util.setSocketAddress

object ServerEditorCommand: Command(
    "bcservereditor",
    "bungeecordservereditor.command.bcservereditor",
    "bcse",
), TabExecutor {
    private val commands = listOf("add", "edit", "remove")

    private fun CommandSender.sendHelp() {
        send("${ChatColor.YELLOW}----------------------------------------")
        send("${ChatColor.AQUA}/bcse ${ChatColor.GRAY}- ${ChatColor.GREEN}this")
        send("${ChatColor.AQUA}/bcse add <server> <address> [restricted = false] ${ChatColor.GRAY}- ${ChatColor.GREEN}サーバーを追加")
        send("${ChatColor.AQUA}/bcse edit <server> <address> [restricted = false] ${ChatColor.GRAY}- ${ChatColor.GREEN}サーバーを編集")
        send("${ChatColor.AQUA}/bcse remove <server> ${ChatColor.GRAY}- ${ChatColor.GREEN}サーバーを削除")
        send("${ChatColor.YELLOW}----------------------------------------")
    }

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) return sender.sendHelp()
        when (args[0]) {
            "add" -> {
                if (args.size <= 2) return sender.sendHelp()
                val name = args[1]
                val address = args[2]
                val restricted = args.getOrElse(3) { "false" }.toBoolean()
                try {
                    val server = ProxyServer.getInstance().constructServerInfo(name, createInetSocketAddress(address), "", restricted)
                    ProxyServer.getInstance().config.servers[name] = server
                    sender.send("${ChatColor.GREEN}サーバー「${ChatColor.YELLOW}$name${ChatColor.GREEN}」を追加しました。")
                } catch (e: RuntimeException) {
                    sender.send("${ChatColor.RED}" + (e.message ?: "Unknown error message. Please check the console for errors."))
                    e.printStackTrace()
                }
            }
            "edit" -> {
                if (args.size <= 2) return sender.sendHelp()
                val name = args[1]
                val address = args[2]
                val restricted = args.getOrElse(3) { "false" }.toBoolean()
                val server = ProxyServer.getInstance().getServerInfo(name)
                    ?: return sender.send("${ChatColor.RED}サーバー「${ChatColor.YELLOW}$name${ChatColor.RED}」が見つかりません。")
                if (server::class.java.canonicalName != "net.md_5.bungee.BungeeServerInfo") {
                    return sender.send("${ChatColor.RED}サーバー「${ChatColor.YELLOW}$name${ChatColor.RED}」は編集できません。")
                }
                try {
                    server.setSocketAddress(createInetSocketAddress(address))
                    server.setRestricted(restricted)
                    sender.send("${ChatColor.GREEN}サーバー「${ChatColor.YELLOW}$name${ChatColor.GREEN}」の情報を編集しました。")
                } catch (e: RuntimeException) {
                    sender.send("${ChatColor.RED}" + (e.message ?: "??? (pls check console)"))
                    e.printStackTrace()
                }
            }
            "remove" -> {
                if (args.size <= 1) return sender.sendHelp()
                val name = args[1]
                if (ProxyServer.getInstance().config.servers.remove(name) != null) {
                    sender.send("${ChatColor.GREEN}サーバー「${ChatColor.YELLOW}$name${ChatColor.GREEN}」を削除しました。")
                } else {
                    sender.send("${ChatColor.RED}サーバー「${ChatColor.YELLOW}$name${ChatColor.RED}」を削除できませんでした。")
                }
            }
            else -> sender.sendHelp()
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<String>): Iterable<String> {
        if (args.isEmpty()) return emptyList()
        if (args.size == 1) return commands.filter(args[0])
        if ((args[0] == "edit" || args[0] == "remove") && args.size == 2) {
            return ProxyServer.getInstance().servers.keys.toList().filter(args[1])
        }
        return emptyList()
    }

    private fun List<String>.filter(s: String): List<String> = filter { s1 -> s1.lowercase().startsWith(s.lowercase()) }
}
