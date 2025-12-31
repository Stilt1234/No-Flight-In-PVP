package org.stilt34.noFlightInPVP.event_listener

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.stilt34.noFlightInPVP.NoFlightInPVP

class DisableElytraAndTridentTimerOnPlayerLeaveEventListener : Listener {

    @EventHandler
    fun onPlayerLeave(event : PlayerQuitEvent)
    {
        if(NoFlightInPVP.getElytraFlightAllowed() == true) { return }

        val key = NamespacedKey(NoFlightInPVP.Companion.getPluginInstance(), "timer_enabled")
        
        if(NoFlightInPVP.Companion.timerMap[event.player.displayName().toString()] != null ||
            (event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) != null &&
                    event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) == true))
        {
            NoFlightInPVP.Companion.timerMap[event.player.displayName().toString()]!!.interrupt()
            NoFlightInPVP.Companion.timerMap.remove(event.player.displayName().toString())

            val keyEventPlayer = NamespacedKey(NoFlightInPVP.Companion.getPluginInstance(), "timer_enabled")
            event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, true)

            NoFlightInPVP.Companion.getPluginInstance().logger.info("${event.player.name} has logged out during PVP. Either Elytra/Elytra's or Trident/Trident's has been taken.")
        }
    }

}