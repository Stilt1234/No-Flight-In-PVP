package org.stilt34.noFlightInPVP.event_listener

import com.jeff_media.morepersistentdatatypes.DataType
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import net.kyori.adventure.text.Component.text
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.stilt34.noFlightInPVP.NoFlightInPVP
import org.stilt34.noFlightInPVP.NoFlightInPVP.Companion.getPluginInstance
import org.stilt34.noFlightInPVP.util.NoFlightInPVPUtils

class DisableElytraAndTridentOnPlayerHitEventListener : Listener
{

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerHit (event : PrePlayerAttackEntityEvent)
    {
        if (NoFlightInPVP.getElytraFlightAllowed() == true && NoFlightInPVP.getTridentRiptideAllowed() == true)
        {
            return
        }
        if(NoFlightInPVP.getHitTimer() <= 0) { return }
        if(event.attacked !is Player || !(event.willAttack()))
        {
            return
        }
        else if((event.attacked as Player).gameMode.equals(GameMode.CREATIVE) || event.player.gameMode.equals(GameMode.CREATIVE))
        {
            return
        }
        else
        {
            // Hit Player Code
            val hitPlayer = event.attacked as? Player

            val hitPlayerElytraListKey = NamespacedKey(getPluginInstance(), "elytra_list")
            val hitPlayerTridentListKey = NamespacedKey(getPluginInstance(), "trident_list")

            if(hitPlayer == null) {return}

            if(NoFlightInPVP.timerMap[hitPlayer.displayName().toString()] != null)
            {
                NoFlightInPVP.timerMap[hitPlayer.displayName().toString()]!!.interrupt()
                NoFlightInPVP.timerMap.remove(hitPlayer.displayName().toString())
            }

            if(NoFlightInPVP.timerMap[event.player.displayName().toString()] != null)
            {
                NoFlightInPVP.timerMap[event.player.displayName().toString()]!!.interrupt()
                NoFlightInPVP.timerMap.remove(event.player.displayName().toString())
            }

            hitPlayer.setCooldown(Material.ELYTRA, NoFlightInPVP.getHitTimer() * NoFlightInPVP.getTickRate().toInt())

            val keyHitPlayer = NamespacedKey(getPluginInstance(), "timer_enabled")
            hitPlayer.persistentDataContainer.set(keyHitPlayer, PersistentDataType.BOOLEAN, true)

            val keyShowTimerText = NamespacedKey(getPluginInstance(), "show_timer_text")

            val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
            val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")

            val threadHitPlayer = Thread(
                Runnable
                {
                    val hitPlayer90sTimerKey = NamespacedKey(getPluginInstance(), "timer_90s")

                    if(hitPlayer.persistentDataContainer.get(hitPlayer90sTimerKey, PersistentDataType.BOOLEAN) != null
                       && hitPlayer.persistentDataContainer.get(hitPlayer90sTimerKey, PersistentDataType.BOOLEAN) == true)
                    {
                        var timeHitPlayer = NoFlightInPVP.getCombatLogTimer()

                        repeat(NoFlightInPVP.getCombatLogTimer())
                        {
                            var h_showTimerText =
                                    hitPlayer.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)

                            var h_actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(
                                hitPlayer.persistentDataContainer.get(
                                    keyActionBarColour,
                                    PersistentDataType.STRING
                                )!!
                            )

                            if(Thread.currentThread().isInterrupted == false)
                            {
                                hitPlayer.isGliding = false
                                if ((hitPlayer.inventory.contains(Material.ELYTRA) || (hitPlayer.inventory.chestplate != null && hitPlayer.inventory.chestplate!!.type.equals(
                                        Material.ELYTRA
                                    ))) && h_showTimerText == true)
                                {
                                    hitPlayer.sendActionBar(
                                        text().content("${timeHitPlayer}s").color(h_actionBarColour).build()
                                    )
                                }
                                if (timeHitPlayer == NoFlightInPVP.getHitTimer())
                                {
                                    hitPlayer.persistentDataContainer.set(
                                        hitPlayer90sTimerKey,
                                        PersistentDataType.BOOLEAN,
                                        false
                                    )
                                }
                                timeHitPlayer -= 1
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
                    }
                    else
                    {
                        var timeHitPlayer = NoFlightInPVP.getHitTimer()

                        repeat(NoFlightInPVP.getHitTimer())
                        {
                            var h_showTimerText =
                                    hitPlayer.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)

                            var h_actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(
                                hitPlayer.persistentDataContainer.get(
                                    keyActionBarColour,
                                    PersistentDataType.STRING
                                )!!
                            )

                            if(Thread.currentThread().isInterrupted == false)
                            {
                                hitPlayer.isGliding = false
                                if ((hitPlayer.persistentDataContainer.get(
                                        hitPlayerElytraListKey,
                                        DataType.asList(DataType.ITEM_STACK)
                                    ) != null
                                     || hitPlayer.persistentDataContainer.get(
                                        hitPlayerTridentListKey,
                                        DataType.asList(DataType.ITEM_STACK)
                                    ) != null)
                                    && h_showTimerText == true)
                                {
                                    hitPlayer.sendActionBar(
                                        text().content("${timeHitPlayer}s").color(h_actionBarColour).build()
                                    )
                                }
                                if (timeHitPlayer == NoFlightInPVP.getHitTimer())
                                {
                                    hitPlayer.persistentDataContainer.set(
                                        hitPlayer90sTimerKey,
                                        PersistentDataType.BOOLEAN,
                                        false
                                    )
                                }
                                timeHitPlayer -= 1
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
                    }

                    if(Thread.currentThread().isInterrupted == false)
                    {
                        var h_showText =
                                hitPlayer.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)

                        var h_actionBarText =
                                hitPlayer.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!
                        var h_actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(
                            hitPlayer.persistentDataContainer.get(
                                keyActionBarColour,
                                PersistentDataType.STRING
                            )!!
                        )

                        if ((hitPlayer.persistentDataContainer.get(
                                hitPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK)
                            ) != null
                             || hitPlayer.persistentDataContainer.get(
                                hitPlayerTridentListKey, DataType.asList(DataType.ITEM_STACK)
                            ) != null)
                            && h_showText == true)
                        {
                            hitPlayer.sendActionBar(text().content(h_actionBarText).color(h_actionBarColour).build())
                        }
                        hitPlayer.persistentDataContainer.set(keyHitPlayer, PersistentDataType.BOOLEAN, false)
                        NoFlightInPVP.timerMap.remove(hitPlayer.displayName().toString())
                    }
                }
            )

            NoFlightInPVP.timerMap.put(hitPlayer.displayName().toString(), threadHitPlayer)
            if(hitPlayer.inventory.chestplate != null && hitPlayer.inventory.chestplate!!.type.equals(Material.ELYTRA))
            {
                var hitPlayerElytraList = hitPlayer.persistentDataContainer.get(hitPlayerElytraListKey, DataType.asList(
                    DataType.ITEM_STACK))
                if(hitPlayerElytraList == null)
                {
                    hitPlayerElytraList = mutableListOf(hitPlayer.inventory.chestplate!!.clone())
                }
                else
                {
                    hitPlayerElytraList.add(hitPlayer.inventory.chestplate!!.clone())
                }
                hitPlayer.persistentDataContainer.set(hitPlayerElytraListKey, DataType.asList(DataType.ITEM_STACK), hitPlayerElytraList)
                hitPlayer.inventory.chestplate = null
            }
            if ((hitPlayer.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                 && hitPlayer.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE))
                || (hitPlayer.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                    && hitPlayer.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
            {
                var hitPlayerTridentList = hitPlayer.persistentDataContainer.get(
                    hitPlayerTridentListKey, DataType.asList(
                        DataType.ITEM_STACK
                    )
                )
                if (hitPlayerTridentList == null)
                {
                    hitPlayerTridentList = mutableListOf(
                        hitPlayer.inventory.itemInMainHand.clone(),
                        hitPlayer.inventory.itemInOffHand.clone()
                    ).filter { i -> i.type.equals(Material.TRIDENT) && i.enchantments.contains((Enchantment.RIPTIDE)) }
                }
                else
                {
                    hitPlayerTridentList.add(hitPlayer.inventory.itemInMainHand.clone())
                    hitPlayerTridentList.add(hitPlayer.inventory.itemInOffHand.clone())
                    hitPlayerTridentList = hitPlayerTridentList.filter { i ->
                        i.type.equals(Material.TRIDENT) && i.enchantments.contains((Enchantment.RIPTIDE))
                    }
                }
                hitPlayer.persistentDataContainer.set(
                    hitPlayerTridentListKey,
                    DataType.asList(DataType.ITEM_STACK),
                    hitPlayerTridentList
                )
                if ((hitPlayer.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                     && hitPlayer.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE)))
                {
                    hitPlayer.inventory.remove(hitPlayer.inventory.itemInMainHand)
                }
                if ((hitPlayer.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                     && hitPlayer.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
                {
                    hitPlayer.inventory.setItemInOffHand(ItemStack(Material.AIR))
                }
            }
            threadHitPlayer.start()



            // Attacking Player Code
            event.player.setCooldown(Material.ELYTRA, NoFlightInPVP.getHitTimer() * NoFlightInPVP.getTickRate().toInt())

            val keyEventPlayer = NamespacedKey(getPluginInstance(), "timer_enabled")
            event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, true)

            val eventPlayerElytraListKey = NamespacedKey(getPluginInstance(), "elytra_list")
            val eventPlayerTridentListKey = NamespacedKey(getPluginInstance(), "trident_list")

            val threadEventPlayer = Thread(
                Runnable
                {
                    val eventPlayer90sTimerKey = NamespacedKey(getPluginInstance(), "timer_90s")
                    
                    if(event.player.persistentDataContainer.get(eventPlayer90sTimerKey, PersistentDataType.BOOLEAN) != null
                       && event.player.persistentDataContainer.get(eventPlayer90sTimerKey, PersistentDataType.BOOLEAN) == true)
                    {
                        var timeEventPlayer = NoFlightInPVP.getCombatLogTimer()

                        repeat(NoFlightInPVP.getCombatLogTimer())
                        {
                            var showTimerText =
                                    event.player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)

                            var actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(
                                event.player.persistentDataContainer.get(
                                    keyActionBarColour,
                                    PersistentDataType.STRING
                                )!!
                            )

                            if(Thread.currentThread().isInterrupted == false)
                            {
                                event.player.isGliding = false
                                if ((event.player.inventory.contains(Material.ELYTRA) || (event.player.inventory.chestplate != null && event.player.inventory.chestplate!!.type.equals(
                                        Material.ELYTRA
                                    ))) && showTimerText == true)
                                {
                                    event.player.sendActionBar(
                                        text().content("${timeEventPlayer}s").color(actionBarColour).build()
                                    )
                                }
                                if (timeEventPlayer == NoFlightInPVP.getHitTimer())
                                {
                                    event.player.persistentDataContainer.set(
                                        eventPlayer90sTimerKey,
                                        PersistentDataType.BOOLEAN,
                                        false
                                    )
                                }
                                timeEventPlayer -= 1
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
                    }
                    else
                    {
                        var timeEventPlayer = NoFlightInPVP.getHitTimer()

                        repeat(NoFlightInPVP.getHitTimer())
                        {
                            var showTimerText =
                                    event.player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)

                            var actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(
                                event.player.persistentDataContainer.get(
                                    keyActionBarColour,
                                    PersistentDataType.STRING
                                )!!
                            )

                            if(Thread.currentThread().isInterrupted == false)
                            {
                                event.player.isGliding = false
                                if ((event.player.persistentDataContainer.get(
                                        eventPlayerElytraListKey,
                                        DataType.asList(DataType.ITEM_STACK)
                                    ) != null
                                     || event.player.persistentDataContainer.get(
                                        eventPlayerTridentListKey,
                                        DataType.asList(DataType.ITEM_STACK)
                                    ) != null) && showTimerText == true)
                                {
                                    event.player.sendActionBar(
                                        text().content("${timeEventPlayer}s").color(actionBarColour).build()
                                    )
                                }
                                if (timeEventPlayer == NoFlightInPVP.getHitTimer())
                                {
                                    event.player.persistentDataContainer.set(
                                        eventPlayer90sTimerKey,
                                        PersistentDataType.BOOLEAN,
                                        false
                                    )
                                }
                                timeEventPlayer -= 1
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
                    }

                    if(Thread.currentThread().isInterrupted == false)
                    {
                        var showText =
                                event.player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)

                        var actionBarText =
                                event.player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!
                        var actionBarColour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(
                            event.player.persistentDataContainer.get(
                                keyActionBarColour,
                                PersistentDataType.STRING
                            )!!
                        )

                        if ((event.player.persistentDataContainer.get(
                                eventPlayerElytraListKey,
                                DataType.asList(DataType.ITEM_STACK)
                            ) != null
                             || event.player.persistentDataContainer.get(
                                eventPlayerTridentListKey,
                                DataType.asList(DataType.ITEM_STACK)
                            ) != null) && showText == true)
                        {
                            event.player.sendActionBar(
                                text().content(actionBarText).color(actionBarColour).build()
                            )
                        }
                        event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, false)
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
            if ((event.player.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                 && event.player.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE))
                || (event.player.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                    && event.player.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
            {
                var eventPlayerTridentList = event.player.persistentDataContainer.get(
                    eventPlayerTridentListKey, DataType.asList(
                        DataType.ITEM_STACK
                    )
                )
                if (eventPlayerTridentList == null)
                {
                    eventPlayerTridentList = mutableListOf(
                        event.player.inventory.itemInMainHand.clone(),
                        event.player.inventory.itemInOffHand.clone()
                    ).filter { i -> i.type.equals(Material.TRIDENT) && i.enchantments.contains((Enchantment.RIPTIDE)) }
                }
                else
                {
                    eventPlayerTridentList.add(event.player.inventory.itemInMainHand.clone())
                    eventPlayerTridentList.add(event.player.inventory.itemInOffHand.clone())
                    eventPlayerTridentList = eventPlayerTridentList.filter { i ->
                        i.type.equals(Material.TRIDENT) && i.enchantments.contains((Enchantment.RIPTIDE))
                    }
                }
                event.player.persistentDataContainer.set(
                    eventPlayerTridentListKey,
                    DataType.asList(DataType.ITEM_STACK),
                    eventPlayerTridentList
                )
                if ((event.player.inventory.itemInMainHand.type.equals(Material.TRIDENT)
                     && event.player.inventory.itemInMainHand.enchantments.contains(Enchantment.RIPTIDE)))
                {
                    event.player.inventory.remove(event.player.inventory.itemInMainHand)
                }
                if ((event.player.inventory.itemInOffHand.type.equals(Material.TRIDENT)
                     && event.player.inventory.itemInOffHand.enchantments.contains(Enchantment.RIPTIDE)))
                {
                    event.player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                }
            }
            threadEventPlayer.start()
        }
    }
}