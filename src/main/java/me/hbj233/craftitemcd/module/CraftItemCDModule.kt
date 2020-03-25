package me.hbj233.craftitemcd.module

import cn.nukkit.Player
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.inventory.CraftItemEvent
import cn.nukkit.item.Item
import me.hbj233.craftitemcd.CraftItemCDPlugin
import top.wetabq.easyapi.api.defaults.*
import top.wetabq.easyapi.config.defaults.SimpleConfigEntry
import top.wetabq.easyapi.module.ModuleInfo
import top.wetabq.easyapi.module.ModuleVersion
import top.wetabq.easyapi.module.SimpleEasyAPIModule
import top.wetabq.easyapi.module.defaults.ScreenShowModule
import top.wetabq.easyapi.screen.ScreenShow
import top.wetabq.easyapi.screen.ShowType
import top.wetabq.easyapi.utils.color

object CraftItemCDModule : SimpleEasyAPIModule() {

    private const val MODULE_NAME = "CraftItemCDModule"
    private const val AUTHOR = "HBJ233"
    const val SIMPLE_CONFIG = "craftItemCDSimpleConfig"
    const val CRAFT_ITEM_CD_LISTENER = "craftItemCDListener"
    const val CRAFT_ITEM_CD_FORMAT = "%craftitemcd%"
    private var CRAFT_ITEM_CD = "craftitemcd"
    //Unit: second
    private var craftItemCD : Int = 10
    private var nextCraftItemTime : MutableMap<Player, Long> = mutableMapOf()

    override fun getModuleInfo(): ModuleInfo = ModuleInfo(
            CraftItemCDPlugin.instance,
            MODULE_NAME,
            AUTHOR,
            ModuleVersion(1,0,0)
    )

    override fun moduleRegister() {

        val simpleConfig = this.registerAPI(SIMPLE_CONFIG, SimpleConfigAPI(CraftItemCDPlugin.instance))
                .add(SimpleConfigEntry(CRAFT_ITEM_CD, craftItemCD))

        craftItemCD = simpleConfig.getPathValue(CRAFT_ITEM_CD)?.toString()?.toInt() ?: craftItemCD

        MessageFormatAPI.registerSimpleFormatter(object : SimpleMessageFormatter {
            override fun format(message: String): String = message.replace(CRAFT_ITEM_CD_FORMAT, craftItemCD.toString())
        })

        this.registerAPI(CRAFT_ITEM_CD_LISTENER, NukkitListenerAPI(CraftItemCDPlugin.instance))
                .add(object : Listener {

                    @EventHandler
                    fun onCraftItemEvent(event: CraftItemEvent) {
                        if (!nextCraftItemTime.containsKey(event.player)){
                            nextCraftItemTime[event.player] = System.currentTimeMillis() / 1000 + craftItemCD
                        } else {
                            nextCraftItemTime[event.player]?.let { it ->
                                if (System.currentTimeMillis() / 1000 <= it) {
                                    event.isCancelled = true
                                    val needWaitTime = it - System.currentTimeMillis() / 1000
                                    ScreenShowModule.addScreenShow(ScreenShow(setOf(event.player), "&c&l你在 $needWaitTime 秒内无法合成.".color(), ScreenShowModule.HIGHEST_PRIORITY, 40, 20, false, false, ShowType.TIP))
                                } else {
                                    nextCraftItemTime[event.player] = System.currentTimeMillis() / 1000 + craftItemCD
                                }
                            }
                        }
                    }

                })

    }

    override fun moduleDisable() {
    }

}