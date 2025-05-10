package org.example.noFlightInPVP.event_listener

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.WHITE
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType
import org.example.noFlightInPVP.NoFlightInPVP

class DisableElytraOnHitEventListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerHit (event : PrePlayerAttackEntityEvent)
    {
        if(event.attacked !is Player || (!event.willAttack()))
        {
            return
        }
        else
        {
            val hitPlayer = event.attacked as? Player

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
            hitPlayer.sendMessage(
                text().content("Entered PVP, your Elytra has been disabled for ").color(color(1f, 0f, 0f))
                    .append(text("60 seconds", WHITE)).build())

            val keyHitPlayer = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")
            hitPlayer.persistentDataContainer.set(keyHitPlayer, PersistentDataType.BOOLEAN, true)

            NoFlightInPVP.getPluginInstance().logger.info("Hit Player PDC added : ${hitPlayer.persistentDataContainer.get(keyHitPlayer,
                PersistentDataType.BOOLEAN)}")

            val threadHitPlayer = Thread(
                Runnable
                {
                    var timeHitPlayer = 60

                    repeat(60)
                    {
                        hitPlayer.isGliding = false
                        hitPlayer.sendActionBar(text().content("Time left before Elytra is enabled : $timeHitPlayer seconds").build())
                        timeHitPlayer -= 1
                        Thread.sleep(1000)
                    }

                    hitPlayer.persistentDataContainer.set(keyHitPlayer, PersistentDataType.BOOLEAN, false)
                    NoFlightInPVP.getPluginInstance().logger.info("Hit Player PDC changed : ${hitPlayer.persistentDataContainer.get(keyHitPlayer,
                        PersistentDataType.BOOLEAN)}")

                    hitPlayer.sendActionBar(text().content("Elytra Enabled").build())
                    hitPlayer.sendMessage(text().content("Your Elytra has been enabled.").color(color(0f, 1f, 0f)).build())

                    NoFlightInPVP.timerMap.remove(hitPlayer.displayName().toString())
                }
            )

            NoFlightInPVP.timerMap.put(hitPlayer.displayName().toString(), threadHitPlayer)
            threadHitPlayer.start()

            event.player.setCooldown(Material.ELYTRA, 60 * NoFlightInPVP.getTickRate().toInt())
            event.player.sendMessage(
                text().content("Entered PVP, your Elytra has been disabled for ").color(color(1f, 0f, 0f))
                    .append(text("60 seconds", WHITE)).build()
            )

            val keyEventPlayer = NamespacedKey(NoFlightInPVP.getPluginInstance(), "timer_enabled")
            event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, true)

            NoFlightInPVP.getPluginInstance().logger.info("Event Player PDC added : ${event.player.persistentDataContainer.get(keyEventPlayer,
                PersistentDataType.BOOLEAN)}")

            val threadEventPlayer = Thread(
                Runnable
                {

                    var timeEventPlayer = 60

                    repeat(60)
                    {
                        event.player.isGliding = false
                        event.player.sendActionBar(text().content("Time left before Elytra is enabled : $timeEventPlayer seconds").build())
                        timeEventPlayer -= 1
                        Thread.sleep(1000)
                    }

                    event.player.persistentDataContainer.set(keyEventPlayer, PersistentDataType.BOOLEAN, false)
                    NoFlightInPVP.getPluginInstance().logger.info("Event Player PDC changed : ${event.player.persistentDataContainer.get(keyEventPlayer,
                        PersistentDataType.BOOLEAN)}")

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