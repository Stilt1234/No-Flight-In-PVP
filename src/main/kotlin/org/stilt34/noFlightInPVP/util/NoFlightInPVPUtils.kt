package org.stilt34.noFlightInPVP.util

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextColor.color

class NoFlightInPVPUtils
{
    companion object
    {
        fun rgb_float_arr_to_string(arr: Array<Float>) : String
        {
            return "${arr[0]} ${arr[1]} ${arr[2]}"
        }

        fun pdc_string_to_text_color_parser(string: String) : TextColor?
        {
            if(NamedTextColor.NAMES.keys().contains(string))
            {
                return NamedTextColor.NAMES.value(string)
            }

            // Assuming rgb check has already been done while executing commands

            if(string.contains("."))
            {
                val arr = string.split(" ").map { it.toFloat() }.toTypedArray()

                return color(arr[0], arr[1], arr[2])
            }

            val arr = string.split(" ").map { it.toInt() }.toTypedArray()

            return color(arr[0], arr[1], arr[2])
        }
    }
}