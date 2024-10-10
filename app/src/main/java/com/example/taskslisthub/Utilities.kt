package com.example.taskslisthub

import android.graphics.Color
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

class Utilities {
    companion object{
        /**
         * Changes the shade of a colour by a given factor
         */
        fun lightenColor(hexColor: String, amount: Float = 0.2f): String {
            // Remove the '#' from the hex color if present
            val color = hexColor.removePrefix("#")

            // Convert hex to RGB
            val r = Integer.parseInt(color.substring(0, 2), 16)
            val g = Integer.parseInt(color.substring(2, 4), 16)
            val b = Integer.parseInt(color.substring(4, 6), 16)

            // Lighten the color
            val newR = (r + (255 - r) * amount).coerceAtMost(255f).toInt()
            val newG = (g + (255 - g) * amount).coerceAtMost(255f).toInt()
            val newB = (b + (255 - b) * amount).coerceAtMost(255f).toInt()

            // Convert back to hex
            return String.format("#%02x%02x%02x", newR, newG, newB)
        }

        /**
         * Changes the shade of a colour by a given factor
         */
        fun lightenColor(color: Int, amount: Float = 0.2f): Int {
            // Extract RGB components from the Color object
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            // Lighten the color
            val newR = (r + (255 - r) * amount).coerceAtMost(255f).toInt()
            val newG = (g + (255 - g) * amount).coerceAtMost(255f).toInt()
            val newB = (b + (255 - b) * amount).coerceAtMost(255f).toInt()

            // Return the new color in ARGB format
            return Color.argb(Color.alpha(color), newR, newG, newB)
        }

        /**
         * Returns the suffix for a given number
         * e.g. 1st, 2nd, 3rd, 4th...
         */
        fun getNumberSuffix(num: Int): String{
            if((num%100 >= 11) and (num%100 <= 20)) return "th"
            else if((num-1)%10 == 0) return "st"
            else if ((num-2)%10 == 0) return "nd"
            else if ((num-3)%10 == 0) return "rd"
            else return "th"
        }

        /**
         * Custom title method because Kotlin doesn't have once for some reason
         */
        fun String.title(): String {
            return trim()
                .split("\\s+".toRegex()) // Split the string into words
                .joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase(Locale.ROOT) } // Capitalize the first character
                        .drop(1) // Drop the first character
                        .lowercase(Locale.ROOT) // Convert the remaining characters to lowercase
                        .let { word.first().uppercase(Locale.ROOT) + it } // Reconstruct the word with the first character uppercase
                }
        }

        /**
         * Formats date and time based on custom format
         */
        fun formatDateTime(time: LocalTime? = null, date: LocalDate? = null): String = dateTimeShortWordFormat(time, date)

        /**
         * converts date and time into the form
         * HH:mm weekday nth month YYYY
         */
        fun dateTimeLongWordFormat(time: LocalTime? = null, date: LocalDate? = null): String{
            var timeString = ""
            var dateString = ""
            if (time != null){
                timeString = String.format(
                    "%02d:%02d",
                    time.hour,
                    time.minute
                )
            }

            if (date != null){
                val suffix = Utilities.getNumberSuffix(date.dayOfMonth)
                dateString = String.format(
                    "%s %d%s %s %d",
                    date.dayOfWeek.name.title(),
                    date.dayOfMonth,
                    suffix,
                    date.month.name.title(),
                    date.year
                )
            }

            return String.format(
                "%s %s",
                timeString,
                dateString
            )
        }

        /**
         * converts date and time into the form
         * HH:mm weekday nth month YYYY
         */
        fun dateTimeShortWordFormat(time: LocalTime? = null, date: LocalDate? = null): String{
            var timeString = ""
            var dateString = ""
            if (time != null){
                timeString = String.format(
                    "%02d:%02d",
                    time.hour,
                    time.minute
                )
            }

            if (date != null){
                dateString = String.format(
                    "%s %d %s",
                    date.dayOfWeek.name.title().take(3),
                    date.dayOfMonth,
                    date.month.name.title().take(3)
                )
            }

            return String.format(
                "%s, %s",
                dateString,
                timeString
            )
        }

        /**
         * converts date and time into the form
         * HH:mm DD/MM/YYYY
         */
        fun dateTimeEnglishNumberFormat(time: LocalTime? = null, date: LocalDate? = null): String{
            var timeString = ""
            var dateString = ""
            if (time != null){
                timeString = String.format(
                    "%02d:%02d",
                    time.hour,
                    time.minute
                )
            }

            if (date != null){
                dateString = String.format(
                    "%d/%d/%d",
                    date.dayOfMonth,
                    date.monthValue,
                    date.year
                )
            }

            return String.format(
                "%s %s",
                timeString,
                dateString
            )
        }
    }
}