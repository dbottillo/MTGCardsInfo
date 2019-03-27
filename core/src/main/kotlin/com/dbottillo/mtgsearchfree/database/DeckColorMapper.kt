package com.dbottillo.mtgsearchfree.database

import com.dbottillo.mtgsearchfree.model.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class DeckColorMapper @Inject constructor(val gson: Gson) {

    fun convert(elements: List<ColorMapperType>): List<Color> {
        val allColors: List<Color> = elements.flatMap { type ->
            when (type) {
                is ColorMapperType.Display -> mapDisplay(type.data)
                is ColorMapperType.Identity -> mapIdentity(type.data)
            }
        }
        val colors = mutableListOf<Color>()
        if (allColors.contains(Color.WHITE)) {
            colors.add(Color.WHITE)
        }
        if (allColors.contains(Color.BLUE)) {
            colors.add(Color.BLUE)
        }
        if (allColors.contains(Color.BLACK)) {
            colors.add(Color.BLACK)
        }
        if (allColors.contains(Color.RED)) {
            colors.add(Color.RED)
        }
        if (allColors.contains(Color.GREEN)) {
            colors.add(Color.GREEN)
        }
        return colors.toList()
    }

    private fun mapIdentity(input: String): List<Color> {
        val colors = gson.fromJson<List<String>>(input, object : TypeToken<List<String>>() {}.type)
        return if (colors?.isNotEmpty() == true) {
            colors.map { color -> color.mapColor()
            }
        } else {
            emptyList()
        }
    }

    private fun mapDisplay(input: String): List<Color> {
        return if (input.isNotEmpty()) {
            input.split(",")
                    .filter { it != "null" }
                    .map { color ->
                when (color) {
                    "W", "White" -> Color.WHITE
                    "U", "Blue" -> Color.BLUE
                    "B", "Black" -> Color.BLACK
                    "R", "Red" -> Color.RED
                    "G", "Green" -> Color.GREEN
                    else -> throw UnsupportedOperationException("color not valid")
                }
            }
        } else {
            emptyList()
        }
    }
}

sealed class ColorMapperType {
    data class Identity(val data: String) : ColorMapperType()
    data class Display(val data: String) : ColorMapperType()
}