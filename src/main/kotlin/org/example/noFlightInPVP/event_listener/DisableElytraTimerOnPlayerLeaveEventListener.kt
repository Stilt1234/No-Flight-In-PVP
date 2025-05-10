package org.example.noFlightInPVP.event_listener

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType
import org.example.noFlightInPVP.NoFlightInPVP

class DisableElytraTimerOnPlayerLeaveEventListener : Listener {

    @EventHandler
    fun onPlayerLeave(event : PlayerQuitEvent)
    {
        if(NoFlightInPVP.timerMap[event.player.displayName().toString()] != null)
        {
            NoFlightInPVP.timerMap[event.player.displayName().toString()]!!.interrupt()
            NoFlightInPVP.timerMap.remove(event.player.displayName().toString())

            val keyEventPlayer = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")
            event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, true)

            NoFlightInPVP.getPluginInstance().logger.info("Event Player PDC added : ${event.player.persistentDataContainer.get(keyEventPlayer,
                PersistentDataType.BOOLEAN)}")
        }
    }

}