package org.example.noFlightInPVP.event_listener

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
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
                    if(event.player.inventory.contains(Material.ELYTRA) || (event.player.inventory.chestplate != null && event.player.inventory.chestplate!!.type.equals(Material.ELYTRA)))
                    {
                        event.player.sendMessage(
                            text().content("You thought you was sneaky, Huh? As punishment, your Elytra has been disabled for ").color(color(1f, 0f, 0f))
                                .append(text("90 seconds", WHITE)).build()
                        )
                    }

                    var timePlayer = 90

                    repeat(90)
                    {
                        event.player.isGliding = false
                        if(event.player.inventory.contains(Material.ELYTRA) || (event.player.inventory.chestplate != null && event.player.inventory.chestplate!!.type.equals(Material.ELYTRA)))
                        {
                            event.player.sendActionBar(
                                text().content("Elytra will be enabled in : ").color(color(1f, 0f, 0f)).append(text("${timePlayer}s",
                                    NamedTextColor.GREEN)).build()
                            )
                        }
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

                    if(event.player.inventory.contains(Material.ELYTRA) || (event.player.inventory.chestplate != null && event.player.inventory.chestplate!!.type.equals(Material.ELYTRA)))
                    {
                        event.player.sendActionBar(text().content("Elytra Enabled").color(color(0f, 1f, 0f)).build())
                        event.player.sendMessage(
                            text().content("Your Elytra has been enabled.").color(color(0f, 1f, 0f)).build()
                        )
                    }

                    NoFlightInPVP.timerMap.remove(event.player.displayName().toString())
                }
            )

            NoFlightInPVP.timerMap.put(event.player.displayName().toString(), threadEventPlayer)
            threadEventPlayer.start()
        }
    }
    
}