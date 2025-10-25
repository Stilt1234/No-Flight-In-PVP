package org.stilt34.noFlightInPVP

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextColor.color
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.stilt34.noFlightInPVP.event_listener.AddItemRemovingTaskOnPlayerJoinEventListener
import org.stilt34.noFlightInPVP.event_listener.CancelItemRemovingTaskOnPlayerLeaveEventListener
import org.stilt34.noFlightInPVP.event_listener.DisableElytraAndTridentOnPlayerHitEventListener
import org.stilt34.noFlightInPVP.event_listener.DisableElytraAndTridentOnPlayerJoinEventListener
import org.stilt34.noFlightInPVP.event_listener.DisableElytraAndTridentTimerOnPlayerLeaveEventListener
import org.stilt34.noFlightInPVP.util.NoFlightInPVPUtils

class NoFlightInPVP : org.bukkit.plugin.java.JavaPlugin() {

    val logger = this.componentLogger
    val pluginName = "No Flight In PVP"
    val helpMenuJsonString = "[{\"text\":\"|\",\"color\":\"gold\"},{\"text\":\"------\",\"color\":\"yellow\"},{\"text\":\" No Flight In PVP\",\"color\":\"#FF0005\"},{\"text\":\" ------\",\"color\":\"yellow\"},{\"text\":\"|\",\"color\":\"gold\"},{\"text\":\" \\n\"},{\"text\":\"Help Menu\",\"color\":\"green\"},{\"text\":\" \\n\"},{\"text\":\"/nfip_config\",\"color\":\"gold\"},{\"text\":\" : opens up this help menu \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config\",\"color\":\"gold\"},{\"text\":\" help\",\"color\":\"gold\"},{\"text\":\" : opens up this help menu \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config show_timer_text <boolean_value>\",\"color\":\"gold\"},{\"text\":\" : enables or disables only timer text (default value is true) \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config show_text <boolean_value>\",\"color\":\"gold\"},{\"text\":\" : enables or disables all text shown in pvp (default value is true) \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config action_bar_text <string_value>\",\"color\":\"gold\"},{\"text\":\" : sets the text to be shown in the action bar (default value is \\\"Elytra and Trident Enabled.\\\")\", \"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config action_bar_colour named_colour <string_value>\", \"color\":\"gold\"},{\"text\":\" : sets the colour of the action bar (default value is green)\", \"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config action_bar_colour rgb <integer_or_float_value> <integer_or_float_value> <integer_or_float_value>\",\"color\":\"gold\"},{\"text\":\" : sets the colour of the action bar in rgb format, whose values can be either integers or floats/decimals (no default value as default for action_bar_colour is green)\",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config hit_timer <integer_value>\",\"color\":\"gold\"},{\"text\":\" : sets the timer seconds in pvp (default value is 60) \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config combat_log_timer <integer_value>\",\"color\":\"gold\"},{\"text\":\" : sets the timer seconds after combat relogin (default value is 90) \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config allow_elytra_flight <boolean_value> \",\"color\":\"gold\"},{\"text\":\": enable or disable elytra flight in pvp (default value is false) \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"/nfip_config allow_trident_riptide <boolean_value>\",\"color\":\"gold\"},{\"text\":\" : enable or disable trident riptide in pvp (default value is false)\",\"color\":\"white\"},{\"text\":\"\\n\\n\"},{\"text\":\"Values\",\"color\":\"yellow\"},{\"text\":\" : \"},{\"text\":\"\\n\"},{\"text\":\"boolean_value\",\"color\":\"gold\"},{\"text\":\" = true/false \",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"integer_value \",\"color\":\"gold\"},{\"text\":\" = 1-infinity in seconds (anything less than or equal to 0 will disable the timer)\",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"integer_or_float_value \",\"color\":\"gold\"},{\"text\":\" = any integer or float/decimal value between 0-256 (excluding 0 and 256)\",\"color\":\"white\"},{\"text\":\"\\n\"},{\"text\":\"string_value\",\"color\":\"gold\"},{\"text\":\" = Anything qouted or unqouted (enclosed or not enclosed in double quotes : \\\"\\\")\",\"color\":\"white\"},{\"text\":\"\\n\\n\"},{\"text\":\"In order to understand the commands in detail, check out plugin description \",\"color\":\"green\"},{\"text\":\"here.\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://modrinth.com/plugin/no-flight-in-pvp\"}},{\"text\":\" \\n\"},{\"text\":\"|\",\"color\":\"gold\"},{\"text\":\"------\",\"color\":\"yellow\"},{\"text\":\" No Flight In PVP\",\"color\":\"#FF0005\"},{\"text\":\" ------\",\"color\":\"yellow\"},{\"text\":\"|\",\"color\":\"gold\"}]"

    init
    {
        tickRate = server.serverTickManager.tickRate
    }

    companion object
    {
        private var tickRate : Float = 20f

        // Since all instances refer to a static companion of that class, this will point to this companion object.
        private var pluginInstance = NoFlightInPVP()

        private var hit_timer = 60

        private var combat_log_timer = 90

        private var allow_elytra_flight = false

        private var allow_trident_riptide = false

        var timerMap : MutableMap<String, Thread> = mutableMapOf()

        var taskIDMap : MutableMap<String, Int> = mutableMapOf()

        fun getTickRate () : Float
        {
            return tickRate
        }

        fun getPluginInstance() : NoFlightInPVP
        {
            return pluginInstance
        }

        fun getHitTimer() : Int
        {
            return hit_timer
        }

        fun getCombatLogTimer() : Int
        {
            return combat_log_timer
        }

        fun getElytraFlightAllowed() : Boolean
        {
            return allow_elytra_flight
        }

        fun getTridentRiptideAllowed() : Boolean
        {
            return allow_trident_riptide
        }
    }

    override fun onEnable() {
        // Plugin startup logic

        // Change plugin instance to the loaded instance
        pluginInstance = this

        logger.info("${"$pluginName v4.0"} has been enabled.")

        // Saving Config if it doesn't exist
        saveDefaultConfig()

        // Getting values from config
        logger.info("$pluginName is accessing config now.")
        if(config.getInt("hit_timer") != 60) { hit_timer = config.getInt("hit_timer") }
        if(config.getInt("combat_log_timer") != 90) { combat_log_timer =  config.getInt("combat_log_timer") }

        // Making Config Command
        var nfip_config = (Commands.literal("nfip_config")
                .executes
                {
                    ctx ->
                    ctx.source.sender.sendMessage(JSONComponentSerializer.json().deserialize(helpMenuJsonString))
                    return@executes Command.SINGLE_SUCCESS
                })
            .then(Commands.literal("help")
                      .executes
                      {
                          ctx ->
                          ctx.source.sender.sendMessage(JSONComponentSerializer.json().deserialize(helpMenuJsonString))
                          return@executes Command.SINGLE_SUCCESS
                      })
            .then(Commands.literal("show_timer_text")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val player = ctx.source.executor
                          val keyShowTimerText = NamespacedKey(getPluginInstance(), "show_timer_text")
                          if(player != null)
                          {
                              player.sendMessage(text().content("Show Timer Text is ${player.persistentDataContainer.get(keyShowTimerText, PersistentDataType.BOOLEAN)}.").color(color(255, 165, 0)))
                          }

                          return@executes Command.SINGLE_SUCCESS
                      }
                      .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes
                                {
                                    ctx ->
                                    if(ctx.source.executor !is Player)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }
                                    if(ctx.source.sender != ctx.source.executor)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }

                                    val player = ctx.source.executor
                                    val keyShowTimerText = NamespacedKey(getPluginInstance(), "show_timer_text")
                                    val arg = ctx.getArgument("value", Boolean::class.java)
                                    if(player != null)
                                    {
                                        player.persistentDataContainer.set(keyShowTimerText, PersistentDataType.BOOLEAN, arg)
                                        player.sendMessage(text().content("Show Timer Text is set to $arg.").color(NamedTextColor.GREEN))
                                    }

                                    return@executes Command.SINGLE_SUCCESS
                                }))
            .then(Commands.literal("show_text")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val  player = ctx.source.executor
                          val keyShowText = NamespacedKey(getPluginInstance(), "show_text")
                          if(player != null)
                          {
                              player.sendMessage(text().content("Show Text is ${player.persistentDataContainer.get(keyShowText, PersistentDataType.BOOLEAN)}.").color(color(255, 165, 0)))
                          }

                          return@executes Command.SINGLE_SUCCESS
                      }
                      .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes
                                {
                                        ctx ->
                                    if(ctx.source.executor !is Player)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }
                                    if(ctx.source.sender != ctx.source.executor)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }

                                    val player = ctx.source.executor
                                    val keyShowText = NamespacedKey(getPluginInstance(), "show_text")
                                    val arg = ctx.getArgument("value", Boolean::class.java)
                                    if(player != null)
                                    {
                                        player.persistentDataContainer.set(keyShowText, PersistentDataType.BOOLEAN, arg)
                                        player.sendMessage(text().content("Show Text is set to $arg.").color(NamedTextColor.GREEN))
                                    }

                                    return@executes Command.SINGLE_SUCCESS
                                }))
            .then(Commands.literal("action_bar_text")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val player = ctx.source.executor
                          val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
                          val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")
                          if(player != null)
                          {
                              var colour : TextColor

                              if(NamedTextColor.NAMES.value(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!) != null)
                              {
                                  colour = NamedTextColor.NAMES.value(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)!!
                              }
                              else
                              {
                                  colour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)!!
                              }

                              player.sendMessage(text().content("Action Bar Text is ").color(color(255, 165, 0))
                                  .append(text().content("${player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)}").color(colour))
                                  .append(text(".").color(color(255, 165, 0))).build())
                              player.sendActionBar(text().content(player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!).color(colour))
                          }

                          return@executes Command.SINGLE_SUCCESS

                      }
                      .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes
                                {
                                    ctx ->
                                    if(ctx.source.executor !is Player)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }
                                    if(ctx.source.sender != ctx.source.executor)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }

                                    val player = ctx.source.executor
                                    val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
                                    val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")
                                    val arg = ctx.getArgument("value", String::class.java)
                                    if(player != null)
                                    {
                                        player.persistentDataContainer.set(keyActionBarText, PersistentDataType.STRING, arg)
                                        player.sendMessage(text().content("Action Bar Text is set to ").color(NamedTextColor.GREEN)
                                                               .append(text().content(arg).color(NamedTextColor.NAMES.value(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)))
                                                               .append(text().content(".").color(NamedTextColor.GREEN)).build())
                                        player.sendActionBar(text().content(arg).color(NamedTextColor.NAMES.value(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)))
                                    }

                                    return@executes Command.SINGLE_SUCCESS
                                }))
            .then(Commands.literal("action_bar_colour")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val player = ctx.source.executor
                          val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
                          val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")
                          if(player != null)
                          {
                              var colour : TextColor = color(0, 0, 0)

                              if(NamedTextColor.NAMES.value(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!) != null)
                              {
                                  colour = NamedTextColor.NAMES.value(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)!!
                              }
                              else
                              {
                                  colour = NoFlightInPVPUtils.pdc_string_to_text_color_parser(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!)!!
                              }

                              player.sendMessage(text().content("Action Bar Colour is ").color(color(255, 165, 0))
                                                     .append(text().content(player.persistentDataContainer.get(keyActionBarColour, PersistentDataType.STRING)!!).color(colour))
                                                     .append(text().content(".").color(color(255, 165, 0))).build())
                              player.sendActionBar(text().content(player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!).color(colour))
                          }

                          return@executes Command.SINGLE_SUCCESS
                      }
                      .then(Commands.literal("named_colour")
                                .executes
                                {
                                    ctx ->
                                    if(ctx.source.executor !is Player)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }
                                    if(ctx.source.sender != ctx.source.executor)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }

                                    val player = ctx.source.executor
                                    var fString = text().content("")
                                    for (i in NamedTextColor.NAMES.keys())
                                    {
                                        fString.append(text().content(i).color(NamedTextColor.NAMES.value(i))).build()
                                        if(i != NamedTextColor.NAMES.keys().last())
                                        {
                                            fString.append(text().content(", ").color(color(255, 165, 0)).build())
                                        }
                                    }
                                    if(player != null)
                                    {
                                        player.sendMessage(text().content("Available named colour options are : [").color(color(255, 165, 0)).append(fString).append(text().content("].").color(color(255, 165, 0))).build())
                                    }

                                    return@executes Command.SINGLE_SUCCESS
                                }
                                .then(Commands.argument("colour", ArgumentTypes.namedColor())
                                          .executes
                                          {
                                              ctx ->
                                              if(ctx.source.executor !is Player)
                                              {
                                                  ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                                                  return@executes Command.SINGLE_SUCCESS
                                              }
                                              if(ctx.source.sender != ctx.source.executor)
                                              {
                                                  ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                                                  return@executes Command.SINGLE_SUCCESS
                                              }

                                              val player = ctx.source.executor
                                              val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
                                              val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")
                                              val arg = ctx.getArgument("colour", NamedTextColor::class.java)
                                              if(player != null)
                                              {
                                                  player.persistentDataContainer.set(keyActionBarColour, PersistentDataType.STRING, NamedTextColor.NAMES.key(arg)!!)
                                                  player.sendMessage(text().content("Action Bar Colour is set to ").color(NamedTextColor.GREEN)
                                                                         .append(text().content(NamedTextColor.NAMES.key(arg)!!).color(arg))
                                                                         .append(text().content(".").color(color(NamedTextColor.GREEN))).build())
                                                  player.sendActionBar(text().content(player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!).color(arg))
                                              }

                                              return@executes Command.SINGLE_SUCCESS
                                          }))
                      .then(Commands.literal("rgb")
                                .executes
                                {
                                    ctx ->
                                    if(ctx.source.executor !is Player)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }
                                    if(ctx.source.sender != ctx.source.executor)
                                    {
                                        ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                                        return@executes Command.SINGLE_SUCCESS
                                    }

                                    val player = ctx.source.executor

                                    if(player != null)
                                    {
                                        player.sendMessage(text().content("The format for rgb is <value> <value> <value> and value can be either integers (using rgb_int command) or floats (using rgb_float command).").color(color(255, 165, 0)).build())
                                    }

                                    return@executes Command.SINGLE_SUCCESS
                                }
                                .then(Commands.argument("r", FloatArgumentType.floatArg(0f, 255f))
                                .then(Commands.argument("g", FloatArgumentType.floatArg(0f, 255f))
                                .then(Commands.argument("b", FloatArgumentType.floatArg(0f, 255f))
                                          .executes
                                          {
                                              ctx ->
                                              if(ctx.source.executor !is Player)
                                              {
                                                  ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                                                  return@executes Command.SINGLE_SUCCESS
                                              }
                                              if(ctx.source.sender != ctx.source.executor)
                                              {
                                                  ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                                                  return@executes Command.SINGLE_SUCCESS
                                              }

                                              val player = ctx.source.executor
                                              val keyActionBarText = NamespacedKey(getPluginInstance(), "action_bar_text")
                                              val keyActionBarColour = NamespacedKey(getPluginInstance(), "action_bar_colour")
                                              val arg = arrayOf(ctx.getArgument("r", Float::class.java), ctx.getArgument("g", Float::class.java), ctx.getArgument("b", Float::class.java))
                                              if(player != null)
                                              {
                                                  player.persistentDataContainer.set(keyActionBarColour, PersistentDataType.STRING, NoFlightInPVPUtils.rgb_float_arr_to_string(arg))
                                                  player.sendMessage(text().content("Action Bar Colour is set to (").color(color(NamedTextColor.GREEN))
                                                                         .append(text().content(NoFlightInPVPUtils.rgb_float_arr_to_string(arg)).color(color(arg[0], arg[1], arg[2])))
                                                                         .append(text().content(").").color(color(NamedTextColor.GREEN))).build())
                                                  player.sendActionBar(text().content(player.persistentDataContainer.get(keyActionBarText, PersistentDataType.STRING)!!).color(color(arg[0], arg[1], arg[2])))
                                              }

                                              return@executes Command.SINGLE_SUCCESS
                                          }))))
                      )
            .then(Commands.literal("hit_timer")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val  player = ctx.source.executor

                          if(player != null)
                          {
                              player.sendMessage(text().content("Hit Timer is $hit_timer seconds.").color(color(255, 165, 0)))
                          }

                          return@executes Command.SINGLE_SUCCESS
                      }
                      .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .requires { sender -> sender.sender.isOp }
                                .executes
                                { ctx ->
                                    hit_timer = ctx.getArgument("value", Int::class.java)
                                    server.scheduler.runTaskAsynchronously(this,
                                                                           Runnable
                                                                           {
                                                                               config.set("hit_timer", hit_timer)
                                                                               saveConfig()
                                                                           })
                                    ctx.source.executor!!.sendMessage(text().content("Hit Timer is set to $hit_timer seconds.").color(NamedTextColor.GREEN))
                                    return@executes Command.SINGLE_SUCCESS
                                }))
            .then(Commands.literal("combat_log_timer")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val  player = ctx.source.executor

                          if(player != null)
                          {
                              player.sendMessage(text().content("Combat Log Timer is $combat_log_timer seconds.").color(color(255, 165, 0)))
                          }

                          return@executes Command.SINGLE_SUCCESS
                      }
                      .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .requires { sender -> sender.sender.isOp }
                                .executes
                                { ctx ->
                                    combat_log_timer = ctx.getArgument("value", Int::class.java)
                                    server.scheduler.runTaskAsynchronously(this,
                                                                           Runnable
                                                                           {
                                                                               config.set("combat_log_timer", combat_log_timer)
                                                                               saveConfig()
                                                                           })
                                    ctx.source.executor!!.sendMessage(text().content("Combat Log Timer is set to $combat_log_timer seconds.").color(NamedTextColor.GREEN))
                                    return@executes Command.SINGLE_SUCCESS
                                }))
            .then(Commands.literal("allow_elytra_flight")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val  player = ctx.source.executor

                          if(player != null)
                          {
                              player.sendMessage(text().content("Allow Elytra Flight is $allow_elytra_flight.").color(color(255, 165, 0)))
                          }

                          return@executes Command.SINGLE_SUCCESS
                      }
                      .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires { sender -> sender.sender.isOp}
                                .executes
                                {
                                    ctx ->
                                        allow_elytra_flight = ctx.getArgument("value", Boolean::class.java)
                                        server.scheduler.runTaskAsynchronously(this,
                                                                               Runnable
                                                                               {
                                                                                   config.set("allow_elytra_flight", allow_elytra_flight)
                                                                                   saveConfig()
                                                                               })
                                        ctx.source.executor!!.sendMessage(text().content("Allow Elytra Flight is set to $allow_elytra_flight.").color(NamedTextColor.GREEN))
                                        return@executes Command.SINGLE_SUCCESS
                                }))
            .then(Commands.literal("allow_trident_riptide")
                      .executes
                      {
                          ctx ->
                          if(ctx.source.executor !is Player)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command!").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }
                          if(ctx.source.sender != ctx.source.executor)
                          {
                              ctx.source.sender.sendMessage(text().content("Only Players can use this command! Use /execute as if needed.").color(color(255, 165, 0)).build())
                              return@executes Command.SINGLE_SUCCESS
                          }

                          val  player = ctx.source.executor

                          if(player != null)
                          {
                              player.sendMessage(text().content("Allow Trident Riptide is $allow_trident_riptide.").color(color(255, 165, 0)))
                          }

                          return@executes Command.SINGLE_SUCCESS
                      }
                      .then(Commands.argument("value", BoolArgumentType.bool())
                                .requires { sender -> sender.sender.isOp}
                                .executes
                                {
                                    ctx ->
                                    allow_trident_riptide = ctx.getArgument("value", Boolean::class.java)
                                    server.scheduler.runTaskAsynchronously(this,
                                                                           Runnable
                                                                           {
                                                                               config.set("allow_trident_riptide", allow_trident_riptide)
                                                                               saveConfig()
                                                                           })
                                    ctx.source.executor!!.sendMessage(text().content("Allow Trident Riptide is set to $allow_trident_riptide.").color(NamedTextColor.GREEN))
                                    return@executes Command.SINGLE_SUCCESS
                                }))
            .build()

        // Registering Config Command
        logger.info("$pluginName is registering commands now.")
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, {commands ->
            commands.registrar().register(nfip_config)
        })

        // Registering Event Listeners
        logger.info("$pluginName is adding event listeners now.")
        server.pluginManager.registerEvents(DisableElytraAndTridentOnPlayerHitEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraAndTridentOnPlayerJoinEventListener(), this)
        server.pluginManager.registerEvents(DisableElytraAndTridentTimerOnPlayerLeaveEventListener(), this)
        server.pluginManager.registerEvents(AddItemRemovingTaskOnPlayerJoinEventListener(), this)
        server.pluginManager.registerEvents(CancelItemRemovingTaskOnPlayerLeaveEventListener(), this)

        // Done loading, sending message.
        logger.info("$pluginName loading complete.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        
        logger.info("$pluginName has been disabled.")
    }
}