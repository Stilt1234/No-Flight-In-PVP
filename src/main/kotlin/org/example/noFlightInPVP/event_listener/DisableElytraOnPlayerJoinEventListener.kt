package org.example.noFlightInPVP.event_listener

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.WHITE
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import org.example.noFlightInPVP.NoFlightInPVP

class DisableElytraOnPlayerJoinEventListener : Listener {
    
    @EventHandler
    fun onPlayerJoin(event : PlayerJoinEvent)
    {
        val key = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")

        if(NoFlightInPVP.timerMap[event.player.displayName().toString()] != null)
        {
            NoFlightInPVP.timerMap[event.player.displayName().toString()]!!.interrupt()
            NoFlightInPVP.timerMap.remove(event.player.displayName().toString())
        }

        if(event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) != null && event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) == true)
        {
            event.player.setCooldown(Material.ELYTRA, 90 * NoFlightInPVP.getTickRate().toInt())

            val threadEventPlayer = Thread(
                Runnable
                {
                    event.player.sendMessage(
                        text().content("You thought you was sneaky, Huh? As punishment, your Elytra has been disabled for ").color(color(1f, 0f, 0f))
                            .append(text("90 seconds", WHITE)).build()
                    )

                    var timePlayer = 90

                    repeat(90)
                    {
                        event.player.isGliding = false
                        event.player.sendActionBar(text().content("Time left before Elytra is enabled : $timePlayer seconds").build())
                        timePlayer -= 1
                        Thread.sleep(1000)
                    }

                    event.player.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, false)
                    NoFlightInPVP.getPluginInstance().logger.info(
                        "PDC changed : ${
                            event.player.persistentDataContainer.get(
                                key,
                                PersistentDataType.BOOLEAN
                            )
                        }"
                    )

                    event.player.sendActionBar(text().content("Elytra Enabled").build())
                    event.player.sendMessage(text().content("Your Elytra has been enabled.").color(color(0f, 1f, 0f)).build())

                    NoFlightInPVP.timerMap.remove(event.player.displayName().toString())
                }
            )

            NoFlightInPVP.timerMap.put(event.player.displayName().toString(), threadEventPlayer)
            threadEventPlayer.start()
        }
    }
    
}