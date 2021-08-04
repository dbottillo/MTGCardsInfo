package com.dbottillo.mtgsearchfree.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.lang.Integer.parseInt

@Parcelize
data class CMCParam(val operator: String, val numericValue: Int, val stringValues: List<String>) : Parcelable

@Parcelize
data class PTParam(val operator: String, val value: Int) : Parcelable

@SuppressLint("DefaultLocale")
fun cmcParamCreator(operator: String, value: String?): CMCParam? {
    if (value == null || value.isEmpty()) return null
    val input = value.uppercase()

    val numbers = mutableListOf<String>()
    val letters = mutableMapOf<String, Int>()
    input.forEach { char ->
        try {
            numbers.add(parseInt(char.toString()).toString())
        } catch (e: NumberFormatException) {
            val current = letters[char.toString()]
            letters[char.toString()] = current?.plus(1) ?: 1
        }
    }
    var numericValue = if (numbers.size > 0) numbers.fold("", { total, next -> total + next }).toInt() else 0
    val stringValues = mutableListOf<String>()
    if (letters.containsKey("X")) {
        stringValues.add("X")
        letters.remove("X")
    }
    if (numericValue > 0) stringValues.add(numericValue.toString())
    letters.forEach {
        var singleLetter = ""
        for (i in 1..it.value) {
            singleLetter += it.key
        }
        stringValues.add(singleLetter)
        numericValue += it.value
    }
    return CMCParam(operator, numericValue, stringValues)
}

fun ptParamCreator(operator: String, value: String?): PTParam? {
    if (value == null) {
        return null
    }
    if (value == "*") {
        return PTParam("IS", value = -1)
    }
    return try {
        val num = parseInt(value.replace("*", ""))
        PTParam(operator, num)
    } catch (e: NumberFormatException) {
        null
    }
}