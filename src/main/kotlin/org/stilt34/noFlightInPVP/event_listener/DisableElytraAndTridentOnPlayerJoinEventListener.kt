package org.stilt34.noFlightInPVP.event_listener

import com.jeff_media.morepersistentdatatypes.DataType
import net.kyori.adventure.text.Component.text
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
import org.stilt34.noFlightInPVP.util.NoFlightInPVPUtils

class DisableElytraAndTridentOnPlayerJoinEventListener : Listener {
    
    @EventHandler
    fun onPlayerJoin(event : PlayerJoinEvent)
    {
        if(event.player.persistentDataContainer.get(NamespacedKey(getPluginInstance(), "show_timer_text"), PersistentDataType.BOOLEAN) == null)
        {
            event.player.persistentDataContainer.set(NamespacedKey(getPluginInstance(), "show_timer_text"), PersistentDataType.BOOLEAN, true)
        }
        if(event.player.persistentDataContainer.get(NamespacedKey(getPluginInstance(), "show_text"), PersistentDataType.BOOLEAN) == null)
        {
            event.player.persistentDataContainer.set(NamespacedKey(getPluginInstance(), "show_text"), PersistentDataType.BOOLEAN, true)
        }

        if(event.player.persistentDataContainer.get(NamespacedKey(getPluginInstance(), "action_bar_text"), PersistentDataType.STRING) == null)
        {
            event.player.persistentDataContainer.set(NamespacedKey(getPluginInstance(), "action_bar_text"), PersistentDataType.STRING, "Elytra and Trident Enabled.")
        }
        if(event.player.persistentDataContainer.get(NamespacedKey(getPluginInstance(), "action_bar_colour"), PersistentDataType.STRING) == null)
        {
            event.player.persistentDataContainer.set(NamespacedKey(getPluginInstance(), "action_bar_colour"), PersistentDataType.STRING, "green")
        }

        val timerEnabledKey = NamespacedKey(getPluginInstance(), "timer_enabled")

        if(NoFlightInPVP.getElytraFlightAllowed() == true && NoFlightInPVP.getTridentRiptideAllowed() == true) { return }

        val eventPlayerElytraListKey = NamespacedKey(getPluginInstance(), "elytra_list")
        val eventPlayerTridentListKey = NamespacedKey(getPluginInstance(), "trident_list")

        val keyShowTimerText = NamespacedKey(getPluginInstance(), "show_timer_text")
        val keyShowText = NamespacedKey(getPluginInstance(), "show_text")

        val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
        val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")

        var showTimerText = event.player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)
        var showText = event.player.persistentDataContainer.get(keyShowText, PersistentDataType.BOOLEAN)

        var actionBarText = event.player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!
        var actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(event.player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)

        if(NoFlightInPVP.getCombatLogTimer() <= 0)
        {
            event.player.persistentDataContainer.set(timerEnabledKey, PersistentDataType.BOOLEAN, false)
            if ((event.player.persistentDataContainer.get(
                    eventPlayerElytraListKey,
                    DataType.asList(DataType.ITEM_STACK)) != null
                 || event.player.persistentDataContainer.get(
                    eventPlayerTridentListKey,
                    DataType.asList(DataType.ITEM_STACK)) != null)
                && (showTimerText == true || showText == true))
            {
                event.player.sendActionBar(
                    text().content(actionBarText).color(actionBarColour).build()
                )
            }
            return
        }

        if(NoFlightInPVP.timerMap[event.player.displayName().toString()] != null)
        {
            NoFlightInPVP.timerMap[event.player.displayName().toString()]!!.interrupt()
            NoFlightInPVP.timerMap.remove(event.player.displayName().toString())
        }

        if(event.player.persistentDataContainer.get(timerEnabledKey, PersistentDataType.BOOLEAN) != null && event.player.persistentDataContainer.get(timerEnabledKey, PersistentDataType.BOOLEAN) == true)
        {
            event.player.setCooldown(Material.ELYTRA, NoFlightInPVP.getCombatLogTimer() * NoFlightInPVP.getTickRate().toInt())

            val keyShowTimerText = NamespacedKey(getPluginInstance(), "show_timer_text")
            val keyShowText = NamespacedKey(getPluginInstance(), "show_text")

            val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
            val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")

            val threadEventPlayer = Thread(
                Runnable
                {
                    val eventPlayer90sTimerKey = NamespacedKey(getPluginInstance(), "timer_90s")
                    event.player.persistentDataContainer.set(eventPlayer90sTimerKey, PersistentDataType.BOOLEAN, true)

                    var timePlayer = NoFlightInPVP.getCombatLogTimer()

                    repeat(NoFlightInPVP.getCombatLogTimer())
                    {
                        var showTimerText = event.player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)
                        var showText = event.player.persistentDataContainer.get(keyShowText, PersistentDataType.BOOLEAN)

                        var actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(event.player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)

                        if (Thread.currentThread().isInterrupted == false)
                        {
                            event.player.isGliding = false
                            if ((event.player.persistentDataContainer.get(
                                    eventPlayerElytraListKey,
                                    DataType.asList(DataType.ITEM_STACK)) != null
                                 || event.player.persistentDataContainer.get(
                                    eventPlayerTridentListKey,
                                    DataType.asList(DataType.ITEM_STACK)) != null)
                                && (showTimerText == true || showText == true))
                            {
                                event.player.sendActionBar(
                                    text().content("${timePlayer}s").color(actionBarColour).build()
                                )
                            }
                            if (timePlayer == NoFlightInPVP.getHitTimer())
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
                        var showTimerText = event.player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)
                        var showText = event.player.persistentDataContainer.get(keyShowText, PersistentDataType.BOOLEAN)

                        var actionBarText = event.player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!
                        var actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(event.player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)

                        if ((event.player.persistentDataContainer.get(
                                eventPlayerElytraListKey,
                                DataType.asList(DataType.ITEM_STACK)) != null
                             || event.player.persistentDataContainer.get(
                                eventPlayerTridentListKey,
                                DataType.asList(DataType.ITEM_STACK)) != null)
                            && (showTimerText == true || showText == true))
                        {
                            event.player.sendActionBar(
                                text().content(actionBarText).color(actionBarColour).build()
                            )
                        }
                        event.player.persistentDataContainer.set(timerEnabledKey, PersistentDataType.BOOLEAN, false)
                        NoFlightInPVP.timerMap.remove(event.player.displayName().toString())
                    }
                }
            )

            NoFlightInPVP.timerMap.put(event.player.displayName().toString(), threadEventPlayer)
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
            if((event.player.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                && event.player.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE))
               || (event.player.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                   && event.player.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
            {
                var eventPlayerTridentList = event.player.persistentDataContainer.get(eventPlayerTridentListKey, DataType.asList(
                    DataType.ITEM_STACK))
                if(eventPlayerTridentList == null)
                {
                    eventPlayerTridentList = mutableListOf(event.player.inventory.itemInMainHand.clone(), event.player.inventory.itemInOffHand.clone()).filter { i -> i.type.equals(Material.TRIDENT) && i.enchantments.contains((Enchantment.RIPTIDE)) }
                }
                else
                {
                    eventPlayerTridentList.add(event.player.inventory.itemInMainHand.clone())
                    eventPlayerTridentList.add(event.player.inventory.itemInOffHand.clone())
                    eventPlayerTridentList = eventPlayerTridentList.filter { i -> i.type.equals(Material.TRIDENT) && i.enchantments.contains((Enchantment.RIPTIDE)) }
                }
                event.player.persistentDataContainer.set(eventPlayerTridentListKey, DataType.asList(DataType.ITEM_STACK), eventPlayerTridentList)
                if((event.player.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                    && event.player.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE)))
                {
                    event.player.inventory.remove(event.player.inventory.itemInMainHand)
                }
                if((event.player.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                    && event.player.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
                {
                    event.player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                }
            }
            threadEventPlayer.start()
        }
    }
}