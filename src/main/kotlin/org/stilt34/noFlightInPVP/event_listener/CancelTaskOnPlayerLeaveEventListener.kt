package org.stilt34.noFlightInPVP.event_listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.stilt34.noFlightInPVP.NoFlightInPVP

class CancelTaskOnPlayerLeaveEventListener : Listener
{
    @EventHandler
    fun onPLayerLeave(event : PlayerQuitEvent)
    {
        NoFlightInPVP.getPluginInstance().server.scheduler.cancelTask(NoFlightInPVP.taskIDMap.getValue(event.player.displayName().toString()))
        NoFlightInPVP.taskIDMap.remove(event.player.displayName().toString())
    }
}