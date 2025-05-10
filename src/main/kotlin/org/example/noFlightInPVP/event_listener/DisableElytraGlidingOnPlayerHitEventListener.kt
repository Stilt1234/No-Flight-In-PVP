package org.example.noFlightInPVP.event_listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.example.noFlightInPVP.NoFlightInPVP

class DisableElytraGlidingOnPlayerHitEventListener : Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerGlide(event : EntityToggleGlideEvent)
    {
        if(event.isCancelled) {return}
        if(event.entity is Player)
        {
            val player = event.entity as Player

            if(NoFlightInPVP.timerMap[player.displayName().toString()] != null)
            {
                NoFlightInPVP.getPluginInstance().logger.info(NoFlightInPVP.timerMap[player.displayName().toString()].toString())
                player.isGliding = false
                event.isCancelled = true
            }
        }
    }
}