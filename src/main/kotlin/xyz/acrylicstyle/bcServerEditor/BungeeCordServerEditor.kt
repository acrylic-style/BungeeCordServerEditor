package xyz.acrylicstyle.bcServerEditor

import net.md_5.bungee.api.plugin.Plugin

class BungeeCordServerEditor: Plugin() {
    override fun onEnable() {
        proxy.pluginManager.registerCommand(this, ServerEditorCommand)
    }
}
