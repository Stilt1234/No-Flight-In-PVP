package org.stilt34.noFlightInPVP.event_listener

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType

class DisableElytraTimerOnPlayerLeaveEventListener : Listener {

    @EventHandler
    fun onPlayerLeave(event : PlayerQuitEvent)
    {
        val key = NamespacedKey(org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.getPluginInstance(), "timer_enabled")
        
        if(org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.timerMap[event.player.displayName().toString()] != null ||
            (event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) != null &&
                    event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) == true))
        {
            org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.timerMap[event.player.displayName().toString()]!!.interrupt()
            org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.timerMap.remove(event.player.displayName().toString())

            val keyEventPlayer = NamespacedKey(org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.getPluginInstance(), "timer_enabled")
            event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, true)

            org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.getPluginInstance().logger.info("Event Player PDC added : ${event.player.persistentDataContainer.get(keyEventPlayer,
                PersistentDataType.BOOLEAN)}")
        }
    }

}