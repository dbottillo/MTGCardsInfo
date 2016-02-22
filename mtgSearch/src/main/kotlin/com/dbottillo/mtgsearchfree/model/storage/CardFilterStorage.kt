package com.dbottillo.mtgsearchfree.model.storage

import android.content.SharedPreferences
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.resources.CardProperties
import rx.Observable
import javax.inject.Inject

class CardFilterStorage(@Inject var preferences: SharedPreferences) {

    fun load(): Observable<CardFilter> {
        return Observable.defer({
            Observable.just(loadFile())
        });
    }

    private fun loadFile(): CardFilter {
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


}

