package me.hbj233.craftitemcd

import cn.nukkit.plugin.PluginBase
import me.hbj233.craftitemcd.module.CraftItemCDModule
import top.wetabq.easyapi.module.EasyAPIModuleManager

class CraftItemCDPlugin : PluginBase() {
    override fun onEnable() {
        instance = this
        EasyAPIModuleManager.register(CraftItemCDModule)
    }

    companion object {
        lateinit var instance : CraftItemCDPlugin
    }
}