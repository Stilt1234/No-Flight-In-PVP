package org.stilt34.noFlightInPVP

import org.stilt34.noFlightInPVP.event_listener.AddTaskOnPlayerJoinEventListener
import org.stilt34.noFlightInPVP.event_listener.CancelTaskOnPlayerLeaveEventListener
import org.stilt34.noFlightInPVP.event_listener.DisableElytraGlidingOnPlayerHitEventListener
import org.stilt34.noFlightInPVP.event_listener.DisableElytraOnPlayerHitEventListener
import org.stilt34.noFlightInPVP.event_listener.DisableElytraOnPlayerJoinEventListener
import org.stilt34.noFlightInPVP.event_listener.DisableElytraTimerOnPlayerLeaveEventListener

class NoFlightInPVP : org.bukkit.plugin.java.JavaPlugin() {

    val logger = this.componentLogger
    val pluginName = "No Flight In PVP"

    init
    {
        tickRate = server.serverTickManager.tickRate

        pluginInstance = this
    }

    companion object
    {
        private var tickRate : Float = 20f

        private var pluginInstance = NoFlightInPVP()

        var timerMap : MutableMap<String, Thread> = mutableMapOf()

        var taskIDMap : MutableMap<String, Int> = mutableMapOf()

        fun getTickRate () : Float
        {
            return tickRate
        }

        fun getPluginInstance(): NoFlightInPVP {
            return pluginInstance
        }
    }

    override fun onEnable() {
        // Plugin startup logic
        logger.info("${"$pluginName v3.0"} has been enabled.")

        logger.info("$pluginName is adding event listeners now.")
        server.pluginManager.registerEvents(DisableElytraOnPlayerHitEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraOnPlayerJoinEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraTimerOnPlayerLeaveEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraGlidingOnPlayerHitEventListener(), this)
        server.pluginManager.registerEvents(AddTaskOnPlayerJoinEventListener(), this)
        server.pluginManager.registerEvents(CancelTaskOnPlayerLeaveEventListener(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("$pluginName has been disabled.")
    }
}
