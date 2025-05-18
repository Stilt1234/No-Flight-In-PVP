package org.stilt34.noFlightInPVP.event_listener

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import org.stilt34.noFlightInPVP.NoFlightInPVP

class DisableElytraOnPlayerJoinEventListener : Listener {
    
    @EventHandler
    fun onPlayerJoin(event : PlayerJoinEvent)
    {
        val key = NamespacedKey(NoFlightInPVP.Companion.getPluginInstance(), "timer_enabled")

        val eventPlayerElytraListKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "elytra_list")

        if(NoFlightInPVP.Companion.timerMap[event.player.displayName().toString()] != null)
        {
            NoFlightInPVP.Companion.timerMap[event.player.displayName().toString()]!!.interrupt()
            NoFlightInPVP.Companion.timerMap.remove(event.player.displayName().toString())
        }

        if(event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) != null && event.player.persistentDataContainer.get(key, PersistentDataType.BOOLEAN) == true)
        {
            event.player.setCooldown(Material.ELYTRA, 90 * NoFlightInPVP.Companion.getTickRate().toInt())

            val threadEventPlayer = Thread(
                Runnable
                {
                    val eventPlayer90sTimerKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_90s")
                    event.player.persistentDataContainer.set(eventPlayer90sTimerKey, PersistentDataType.BOOLEAN, true)

                    var timePlayer = 90

                    repeat(90)
                    {
                        if (Thread.currentThread().isInterrupted == false)
                        {
                            event.player.isGliding = false
                            if (event.player.persistentDataContainer.get(
                                    eventPlayerElytraListKey,
                                    DataType.asList(DataType.ITEM_STACK)
                                ) != null
                            )
                            {
                                event.player.sendActionBar(
                                    text().content("${timePlayer}s").color(color(0, 255, 0)).build()
                                )
                            }
                            if (timePlayer == 60)
                            {
                                event.player.persistentDataContainer.set(
                                    eventPlayer90sTimerKey,
                                    PersistentDataType.BOOLEAN,
                                    false
                                )
                            }
                            timePlayer -= 1
                            try
                            {
                                Thread.sleep(1000)
                            }
                            catch (e: InterruptedException)
                            {
                                Thread.currentThread().interrupt()
                            }
                        }
                    }

                    if(Thread.currentThread().isInterrupted == false)
                    {
                        if (event.player.persistentDataContainer.get(
                                eventPlayerElytraListKey,
                                DataType.asList(DataType.ITEM_STACK)
                            ) != null
                        )
                        {
                            event.player.sendActionBar(
                                text().content("Elytra Enabled").color(color(0f, 1f, 0f)).build()
                            )
                        }
                        event.player.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, false)
                        NoFlightInPVP.Companion.timerMap.remove(event.player.displayName().toString())
                    }
                }
            )

            NoFlightInPVP.Companion.timerMap.put(event.player.displayName().toString(), threadEventPlayer)
            if(event.player.inventory.chestplate != null && event.player.inventory.chestplate!!.type.equals(Material.ELYTRA))
            {
                var eventPlayerElytraList = event.player.persistentDataContainer.get(eventPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK))
                if(eventPlayerElytraList == null)
                {
                    eventPlayerElytraList = mutableListOf(event.player.inventory.chestplate!!.clone())
                }
                else
                {
                    eventPlayerElytraList.add(event.player.inventory.chestplate!!.clone())
                }
                event.player.persistentDataContainer.set(eventPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK), eventPlayerElytraList)
                event.player.inventory.chestplate = null
            }
            threadEventPlayer.start()
        }
    }
    
}