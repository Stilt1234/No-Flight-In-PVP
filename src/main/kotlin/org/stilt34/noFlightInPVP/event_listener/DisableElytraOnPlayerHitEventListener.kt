package org.stilt34.noFlightInPVP.event_listener

import com.jeff_media.morepersistentdatatypes.DataType
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType
import org.stilt34.noFlightInPVP.NoFlightInPVP

class DisableElytraOnPlayerHitEventListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerHit (event : PrePlayerAttackEntityEvent)
    {
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

            val hitPlayerElytraListKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "elytra_list")

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

            hitPlayer.setCooldown(Material.ELYTRA, 60 * NoFlightInPVP.getTickRate().toInt())

            val keyHitPlayer = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")
            hitPlayer.persistentDataContainer.set(keyHitPlayer, PersistentDataType.BOOLEAN, true)

            val threadHitPlayer = Thread(
                Runnable
                {
                    val hitPlayer90sTimerKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_90s")

                    if(hitPlayer.persistentDataContainer.get(hitPlayer90sTimerKey, PersistentDataType.BOOLEAN) != null
                       && hitPlayer.persistentDataContainer.get(hitPlayer90sTimerKey, PersistentDataType.BOOLEAN) == true)
                    {
                        var timeHitPlayer = 90

                        repeat(90)
                        {
                            if(Thread.currentThread().isInterrupted == false)
                            {
                                hitPlayer.isGliding = false
                                if (hitPlayer.inventory.contains(Material.ELYTRA) || (hitPlayer.inventory.chestplate != null && hitPlayer.inventory.chestplate!!.type.equals(
                                        Material.ELYTRA
                                    ))
                                )
                                {
                                    hitPlayer.sendActionBar(
                                        text().content("${timeHitPlayer}s").color(color(0, 255, 0)).build()
                                    )
                                }
                                if (timeHitPlayer == 60)
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
                        var timeHitPlayer = 60

                        repeat(60)
                        {
                            if(Thread.currentThread().isInterrupted == false)
                            {
                                hitPlayer.isGliding = false
                                if (hitPlayer.persistentDataContainer.get(
                                        hitPlayerElytraListKey,
                                        DataType.asList(DataType.ITEM_STACK)
                                    ) != null
                                )
                                {
                                    hitPlayer.sendActionBar(
                                        text().content("${timeHitPlayer}s").color(color(0, 255, 0)).build()
                                    )
                                }
                                if (timeHitPlayer == 60)
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
                        if (hitPlayer.persistentDataContainer.get(
                                hitPlayerElytraListKey,
                                DataType.asList(DataType.ITEM_STACK)
                            ) != null
                        )
                        {
                            hitPlayer.sendActionBar(text().content("Elytra Enabled").color(color(0f, 1f, 0f)).build())
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
            threadHitPlayer.start()



            // Attacking Player Code
            event.player.setCooldown(Material.ELYTRA, 60 * NoFlightInPVP.getTickRate().toInt())

            val keyEventPlayer = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")
            event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, true)

            val eventPlayerElytraListKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "elytra_list")

            val threadEventPlayer = Thread(
                Runnable
                {
                    val eventPlayer90sTimerKey = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_90s")
                    
                    if(event.player.persistentDataContainer.get(eventPlayer90sTimerKey, PersistentDataType.BOOLEAN) != null
                       && event.player.persistentDataContainer.get(eventPlayer90sTimerKey, PersistentDataType.BOOLEAN) == true)
                    {
                        var timeEventPlayer = 90

                        repeat(90)
                        {
                            if(Thread.currentThread().isInterrupted == false)
                            {
                                event.player.isGliding = false
                                if (event.player.inventory.contains(Material.ELYTRA) || (event.player.inventory.chestplate != null && event.player.inventory.chestplate!!.type.equals(
                                        Material.ELYTRA
                                    ))
                                )
                                {
                                    event.player.sendActionBar(
                                        text().content("${timeEventPlayer}s").color(color(0, 255, 0)).build()
                                    )
                                }
                                if (timeEventPlayer == 60)
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
                        var timeEventPlayer = 60

                        repeat(60)
                        {
                            if(Thread.currentThread().isInterrupted == false)
                            {
                                event.player.isGliding = false
                                if (event.player.persistentDataContainer.get(
                                        eventPlayerElytraListKey,
                                        DataType.asList(DataType.ITEM_STACK)
                                    ) != null
                                )
                                {
                                    event.player.sendActionBar(
                                        text().content("${timeEventPlayer}s").color(color(0, 255, 0)).build()
                                    )
                                }
                                if (timeEventPlayer == 60)
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
            threadEventPlayer.start()
        }
    }
}