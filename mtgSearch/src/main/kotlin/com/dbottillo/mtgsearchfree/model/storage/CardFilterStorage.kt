package com.dbottillo.mtgsearchfree.model.storage

import android.content.SharedPreferences
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.CardProperties

class CardFilterStorage(var preferences: SharedPreferences) {

    fun load(): CardFilter {
        var res = CardFilter()
        res.white = preferences.getBoolean(CardProperties.COLOR_WHITE, true)
        res.blue = preferences.getBoolean(CardProperties.COLOR_BLUE, true)
        res.black = preferences.getBoolean(CardProperties.COLOR_BLACK, true)
        res.red = preferences.getBoolean(CardProperties.COLOR_RED, true)
        res.green = preferences.getBoolean(CardProperties.COLOR_GREEN, true)

        res.artifact = preferences.getBoolean(CardProperties.TYPE_ARTIFACT, true)
        res.land = preferences.getBoolean(CardProperties.TYPE_LAND, true)
        res.eldrazi = preferences.getBoolean(CardProperties.TYPE_ELDRAZI, true)

        res.common = preferences.getBoolean(CardProperties.RARITY_COMMON, true)
        res.uncommon = preferences.getBoolean(CardProperties.RARITY_UNCOMMON, true)
        res.rare = preferences.getBoolean(CardProperties.RARITY_RARE, true)
        res.mythic = preferences.getBoolean(CardProperties.RARITY_MYHTIC, true)

        return res;
    }

    fun sync(filter: CardFilter) {
        preferences.edit()
                .putBoolean(CardProperties.COLOR_WHITE, filter.white)
                .putBoolean(CardProperties.COLOR_BLUE, filter.blue)
                .putBoolean(CardProperties.COLOR_BLACK, filter.black)
                .putBoolean(CardProperties.COLOR_RED, filter.red)
                .putBoolean(CardProperties.COLOR_GREEN, filter.green)
                .putBoolean(CardProperties.TYPE_ARTIFACT, filter.artifact)
                .putBoolean(CardProperties.TYPE_LAND, filter.land)
                .putBoolean(CardProperties.TYPE_ELDRAZI, filter.eldrazi)
                .putBoolean(CardProperties.RARITY_COMMON, filter.common)
                .putBoolean(CardProperties.RARITY_UNCOMMON, filter.uncommon)
                .putBoolean(CardProperties.RARITY_RARE, filter.rare)
                .putBoolean(CardProperties.RARITY_MYHTIC, filter.mythic)
                .apply()
    }


}

