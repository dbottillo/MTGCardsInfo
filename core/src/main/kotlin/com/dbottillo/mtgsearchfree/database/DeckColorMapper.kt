package com.dbottillo.mtgsearchfree.database

import com.dbottillo.mtgsearchfree.model.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class DeckColorMapper @Inject constructor(private val gson: Gson) {

    fun convert(colorsIdentities: List<String>): List<Color> {
        val allColors = colorsIdentities.flatMap { colorIdentity ->
            val colors = gson.fromJson<List<String>>(colorIdentity, object : TypeToken<List<String>>() {}.type)
            if (colors?.isNotEmpty() == true) {
                colors.map { color -> color.mapColor() }
            } else {
                emptyList()
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
}