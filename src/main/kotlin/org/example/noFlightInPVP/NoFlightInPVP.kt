package org.example.noFlightInPVP

import org.bukkit.plugin.java.JavaPlugin
import org.example.noFlightInPVP.event_listener.DisableElytraGlidingOnPlayerHitEventListener
import org.example.noFlightInPVP.event_listener.DisableElytraOnHitEventListener
import org.example.noFlightInPVP.event_listener.DisableElytraOnPlayerJoinEventListener
import org.example.noFlightInPVP.event_listener.DisableElytraTimerOnPlayerLeaveEventListener

class NoFlightInPVP : JavaPlugin() {

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
        logger.info("$pluginName has been enabled.")

        logger.info("$pluginName is adding event listeners now.")
        server.pluginManager.registerEvents(DisableElytraOnHitEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraOnPlayerJoinEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraTimerOnPlayerLeaveEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraGlidingOnPlayerHitEventListener(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("$pluginName has been disabled.")
    }
}
