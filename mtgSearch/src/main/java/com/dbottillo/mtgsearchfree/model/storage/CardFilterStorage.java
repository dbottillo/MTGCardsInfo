package com.dbottillo.mtgsearchfree.model.storage;

import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardProperties;
import com.dbottillo.mtgsearchfree.util.LOG;

public class CardFilterStorage {

    SharedPreferences preferences;

    public CardFilterStorage(SharedPreferences preferences) {
        LOG.d("created");
        this.preferences = preferences;
    }

    public CardFilter load() {
        LOG.d();
        CardFilter res = new CardFilter();
        res.white = preferences.getBoolean(CardProperties.COLOR.WHITE.key, true);
        res.blue = preferences.getBoolean(CardProperties.COLOR.BLUE.key, true);
        res.black = preferences.getBoolean(CardProperties.COLOR.BLACK.key, true);
        res.red = preferences.getBoolean(CardProperties.COLOR.RED.key, true);
        res.green = preferences.getBoolean(CardProperties.COLOR.GREEN.key, true);

        res.artifact = preferences.getBoolean(CardProperties.TYPE.ARTIFACT.key, true);
        res.land = preferences.getBoolean(CardProperties.TYPE.LAND.key, true);
        res.eldrazi = preferences.getBoolean(CardProperties.TYPE.ELDRAZI.key, true);

        res.common = preferences.getBoolean(CardProperties.RARITY.COMMON.key, true);
        res.uncommon = preferences.getBoolean(CardProperties.RARITY.UNCOMMON.key, true);
        res.rare = preferences.getBoolean(CardProperties.RARITY.RARE.key, true);
        res.mythic = preferences.getBoolean(CardProperties.RARITY.MYTHIC.key, true);

        return res;
    }

    public void sync(CardFilter filter) {
        LOG.d();
        preferences.edit()
                .putBoolean(CardProperties.COLOR.WHITE.key, filter.white)
                .putBoolean(CardProperties.COLOR.BLUE.key, filter.blue)
                .putBoolean(CardProperties.COLOR.BLACK.key, filter.black)
                .putBoolean(CardProperties.COLOR.RED.key, filter.red)
                .putBoolean(CardProperties.COLOR.GREEN.key, filter.green)
                .putBoolean(CardProperties.TYPE.ARTIFACT.key, filter.artifact)
                .putBoolean(CardProperties.TYPE.LAND.key, filter.land)
                .putBoolean(CardProperties.TYPE.ELDRAZI.key, filter.eldrazi)
                .putBoolean(CardProperties.RARITY.COMMON.key, filter.common)
                .putBoolean(CardProperties.RARITY.UNCOMMON.key, filter.uncommon)
                .putBoolean(CardProperties.RARITY.RARE.key, filter.rare)
                .putBoolean(CardProperties.RARITY.MYTHIC.key, filter.mythic)
                .apply();
    }


}

