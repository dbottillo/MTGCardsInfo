package com.dbottillo.mtgsearchfree.util

import com.dbottillo.mtgsearchfree.model.Deck
import java.util.Locale

@Suppress("ComplexMethod")
fun String?.adjustCode(): String? {
    val stringToLoad = this?.lowercase()
    return when {
        stringToLoad.equals("10e", ignoreCase = true) -> "e10"
        stringToLoad.equals("9ed", ignoreCase = true) -> "ed9"
        stringToLoad.equals("5dn", ignoreCase = true) -> "dn5"
        stringToLoad.equals("8ed", ignoreCase = true) -> "ed8"
        stringToLoad.equals("7ed", ignoreCase = true) -> "ed7"
        stringToLoad.equals("6ed", ignoreCase = true) -> "ed6"
        stringToLoad.equals("5ed", ignoreCase = true) -> "ed5"
        stringToLoad.equals("4ed", ignoreCase = true) -> "ed4"
        stringToLoad.equals("3ed", ignoreCase = true) -> "ed3"
        stringToLoad.equals("2ed", ignoreCase = true) -> "ed2"
        stringToLoad.equals("2xm", ignoreCase = true) -> "dbm"
        stringToLoad.equals("2x2", ignoreCase = true) -> "dbm2"
        else -> stringToLoad
    }
}