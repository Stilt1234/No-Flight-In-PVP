package org.stilt34.noFlightInPVP.event_listener

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.stilt34.noFlightInPVP.NoFlightInPVP
import org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.getPluginInstance

@Suppress("ReplaceCallWithBinaryOperator")
class AddItemHandlingTaskOnPlayerJoinEventListener : Listener
{
    @EventHandler
    fun onPlayerJoin(event : PlayerJoinEvent)
    {
        var elytraTicks = 0
        var tridentTicks = 0

        val itemRemovingTask = getPluginInstance().server.scheduler.scheduleSyncRepeatingTask(
            getPluginInstance(),
            Runnable
            {
                val player = event.player

                val playerElytraListKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "elytra_list")

                val playerTridentListKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "trident_list")

                val playerTimerEnabledKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")

                val keyShowTimerText = NamespacedKey(getPluginInstance(), "show_timer_text")

                val keyShowText = NamespacedKey(getPluginInstance(), "show_text")

                val showText = event.player.persistentDataContainer.get(keyShowText, PersistentDataType.BOOLEAN)

                var playerElytraList = player.persistentDataContainer.get(playerElytraListKey, DataType.asList(DataType.ITEM_STACK))

                var playerTridentList = player.persistentDataContainer.get(playerTridentListKey, DataType.asList(DataType.ITEM_STACK))

                if(player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN) == null)
                {
                    player.persistentDataContainer.set(keyShowTimerText, PersistentDataType.BOOLEAN, true)
                }
                if(player.persistentDataContainer.get(keyShowText, PersistentDataType.BOOLEAN) == null)
                {
                    player.persistentDataContainer.set(keyShowText, PersistentDataType.BOOLEAN, true)
                }

                if(player.inventory.chestplate != null && player.inventory.chestplate!!.type.equals(Material.ELYTRA)
                   && player.persistentDataContainer.get(playerTimerEnabledKey, PersistentDataType.BOOLEAN) == true
                   && NoFlightInPVP.getElytraFlightAllowed() == false)
                {
                    if(playerElytraList == null)
                    {
                        playerElytraList = mutableListOf(player.inventory.chestplate!!.clone())
                    }
                    else
                    {
                        playerElytraList.add(player.inventory.chestplate!!.clone())
                    }

                    player.persistentDataContainer.set(playerElytraListKey, DataType.asList(DataType.ITEM_STACK), playerElytraList)
                    player.inventory.chestplate = null
                }

                if(((player.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                    && player.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE))
                   || (player.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                       && player.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
                   && player.persistentDataContainer.get(playerTimerEnabledKey, PersistentDataType.BOOLEAN) == true
                   && NoFlightInPVP.getTridentRiptideAllowed() == false)
                {
                    if(playerTridentList == null)
                    {
                        playerTridentList = mutableListOf(player.inventory.itemInMainHand.clone(), player.inventory.itemInOffHand.clone()).filter { i -> i.type.equals(Material.TRIDENT) && i.enchantments.contains((Enchantment.RIPTIDE)) }
                    }
                    else
                    {
                        if((player.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                             && player.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE)))
                        {
                            playerTridentList.add(player.inventory.itemInMainHand.clone())
                        }
                        if((player.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                            && player.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
                        {
                            playerTridentList.add(player.inventory.itemInOffHand.clone())
                        }
                    }
                    player.persistentDataContainer.set(playerTridentListKey, DataType.asList(DataType.ITEM_STACK), playerTridentList)
                    if((player.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                        && player.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE)))
                    {
                        player.inventory.remove(player.inventory.itemInMainHand)
                    }
                    if((player.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                        && player.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
                    {
                        player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                    }
                }

                if(player.persistentDataContainer.get(playerElytraListKey, DataType.asList(DataType.ITEM_STACK)) != null
                   && player.persistentDataContainer.get(playerTimerEnabledKey, PersistentDataType.BOOLEAN) == false)
                {
                    var emptySlot = player.inventory.firstEmpty()

                    if(emptySlot != -1 && emptySlot <= 35)
                    {
                        player.inventory.setItem(emptySlot, playerElytraList!!.last())
                        playerElytraList.removeLast()
                        if(playerElytraList.isEmpty())
                        {
                            player.persistentDataContainer.remove(playerElytraListKey)
                        }
                        else
                        {
                            player.persistentDataContainer.set(playerElytraListKey, DataType.asList(DataType.ITEM_STACK), playerElytraList)
                        }
                    }
                    else
                    {
                        if(elytraTicks == 0)
                        {
                            if(playerElytraList != null && showText == true)
                            {
                                if (playerElytraList.size == 1)
                                {
                                    player.sendMessage(text().content("Please clear a slot from your inventory in order to get your Elytra back.").color(color(255, 174, 66)).build())
                                }
                                else
                                {
                                    player.sendMessage(text().content("Please clear ${playerElytraList.size} slots from your inventory in order to get your Elytra's back.").color(color(255, 174, 66)).build())
                                }
                            }

                            elytraTicks = 5 * NoFlightInPVP.getTickRate().toInt()
                        }
                    }

                    elytraTicks -= 1
                }

                if(player.persistentDataContainer.get(playerTridentListKey, DataType.asList(DataType.ITEM_STACK)) != null
                   && player.persistentDataContainer.get(playerTimerEnabledKey, PersistentDataType.BOOLEAN) == false)
                {
                    var emptySlot = player.inventory.firstEmpty()

                    if(emptySlot != -1 && emptySlot <= 35)
                    {
                        player.inventory.setItem(emptySlot, playerTridentList!!.last())
                        playerTridentList.removeLast()
                        if(playerTridentList.isEmpty())
                        {
                            player.persistentDataContainer.remove(playerTridentListKey)
                        }
                        else
                        {
                            player.persistentDataContainer.set(playerTridentListKey, DataType.asList(DataType.ITEM_STACK), playerTridentList)
                        }
                    }
                    else
                    {
                        if(tridentTicks == 0)
                        {
                            if(playerTridentList != null && showText == true)
                            {
                                if (playerTridentList.size == 1)
                                {
                                    player.sendMessage(text().content("Please clear a slot from your inventory in order to get your Trident back.").color(color(255, 174, 66)).build())
                                }
                                else
                                {
                                    player.sendMessage(text().content("Please clear ${playerTridentList.size} slots from your inventory in order to get your Trident's back.").color(color(255, 174, 66)).build())
                                }
                            }

                            tridentTicks = 5 * NoFlightInPVP.getTickRate().toInt()
                        }
                    }

                    tridentTicks -= 1
                }

            }, 0L, 1L)

        NoFlightInPVP.Companion.taskIDMap.put(event.player.displayName().toString(), itemRemovingTask)
    }
}