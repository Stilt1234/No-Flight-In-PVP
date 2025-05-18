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

@Suppress("ReplaceCallWithBinaryOperator")
class AddTaskOnPlayerJoinEventListener : Listener
{
    @EventHandler
    fun onPlayerJoin(event : PlayerJoinEvent)
    {
        var ticks = 0

        val task = NoFlightInPVP.Companion.getPluginInstance().server.scheduler.scheduleSyncRepeatingTask(
            NoFlightInPVP.Companion.getPluginInstance(),
            Runnable
            {
                val player = event.player

                val eventPlayerElytraListKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "elytra_list")

                val eventPlayerTimerEnabledKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")

                var eventPlayerElytraList = player.persistentDataContainer.get(eventPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK))

                if(player.inventory.chestplate != null && player.inventory.chestplate!!.type.equals(Material.ELYTRA)
                   && player.persistentDataContainer.get(eventPlayerTimerEnabledKey, PersistentDataType.BOOLEAN) == true)
                {
                    if(eventPlayerElytraList == null)
                    {
                        eventPlayerElytraList = mutableListOf(player.inventory.chestplate!!.clone())
                    }
                    else
                    {
                        eventPlayerElytraList.add(player.inventory.chestplate!!.clone())
                    }

                    player.persistentDataContainer.set(eventPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK), eventPlayerElytraList)
                    player.inventory.chestplate = null
                }

                if(player.persistentDataContainer.get(eventPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK)) != null
                   && player.persistentDataContainer.get(eventPlayerTimerEnabledKey, PersistentDataType.BOOLEAN) == false)
                {
                    var emptySlot = player.inventory.firstEmpty()

                    if(emptySlot != -1 && emptySlot <= 35)
                    {
                        player.inventory.setItem(emptySlot, eventPlayerElytraList!!.last())
                        eventPlayerElytraList.removeLast()
                        if(eventPlayerElytraList.isEmpty())
                        {
                            player.persistentDataContainer.remove(eventPlayerElytraListKey)
                        }
                        else
                        {
                            player.persistentDataContainer.set(eventPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK), eventPlayerElytraList)
                        }
                    }
                    else
                    {
                        if(ticks == 0)
                        {
                            if(eventPlayerElytraList != null)
                            {
                                if (eventPlayerElytraList.size == 1)
                                {
                                    player.sendMessage(text().content("Please clear a slot from your inventory in order to get your Elytra back.").color(color(255, 174, 66)).build())
                                }
                                else
                                {
                                    player.sendMessage(text().content("Please clear ${eventPlayerElytraList.size} slots from your inventory in order to get your Elytra's back.").color(color(255, 174, 66)).build())
                                }
                            }

                            ticks = 5 * NoFlightInPVP.getTickRate().toInt()
                        }
                    }

                    ticks -= 1
                }

            }, 0L, 1L)

        NoFlightInPVP.Companion.taskIDMap.put(event.player.displayName().toString(), task)
    }
}