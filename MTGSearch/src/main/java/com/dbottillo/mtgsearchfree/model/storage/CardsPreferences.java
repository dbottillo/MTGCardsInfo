package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.CardProperties;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment;

import java.util.Map;

public class CardsPreferences {

    private static String PREFS_NAME = "Filter";
    private SharedPreferences sharedPreferences;

    public CardsPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public CardFilter load() {
        LOG.d();
        CardFilter res = new CardFilter();
        res.white = sharedPreferences.getBoolean(CardProperties.COLOR.WHITE.key, true);
        res.blue = sharedPreferences.getBoolean(CardProperties.COLOR.BLUE.key, true);
        res.black = sharedPreferences.getBoolean(CardProperties.COLOR.BLACK.key, true);
        res.red = sharedPreferences.getBoolean(CardProperties.COLOR.RED.key, true);
        res.green = sharedPreferences.getBoolean(CardProperties.COLOR.GREEN.key, true);

        res.artifact = sharedPreferences.getBoolean(CardProperties.TYPE.ARTIFACT.key, true);
        res.land = sharedPreferences.getBoolean(CardProperties.TYPE.LAND.key, true);
        res.eldrazi = sharedPreferences.getBoolean(CardProperties.TYPE.ELDRAZI.key, true);

        res.common = sharedPreferences.getBoolean(CardProperties.RARITY.COMMON.key, true);
        res.uncommon = sharedPreferences.getBoolean(CardProperties.RARITY.UNCOMMON.key, true);
        res.rare = sharedPreferences.getBoolean(CardProperties.RARITY.RARE.key, true);
        res.mythic = sharedPreferences.getBoolean(CardProperties.RARITY.MYTHIC.key, true);

        return res;
    }

    public void sync(CardFilter filter) {
        LOG.d();
        sharedPreferences.edit()
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

    public boolean isSortWUBRG() {
        return sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true);
    }

    public void setSortOption(boolean wubrg) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_SORT_WUBRG, wubrg);
        editor.apply();
    }

    public void saveSetPosition(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("setPosition", position);
        editor.apply();
        LOG.e("saving: " + position);
    }

    public int getSetPosition() {
        int pos = sharedPreferences.getInt("setPosition", 0);
        LOG.e("pos: " + pos);
        return 0;
    }

    public boolean showPoison() {
        return sharedPreferences.getBoolean("poison", false);
    }

    public boolean twoHGEnabled() {
        return sharedPreferences.getBoolean(BasicFragment.PREF_TWO_HG_ENABLED, false);
    }

    public boolean screenOn() {
        return sharedPreferences.getBoolean(BasicFragment.PREF_SCREEN_ON, false);
    }

    public void showPoison(boolean show) {
        sharedPreferences.edit().putBoolean("poison", show).apply();
    }

    public void setScreenOn(boolean on) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_SCREEN_ON, on);
        editor.apply();
    }

    public void setTwoHGEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_TWO_HG_ENABLED, enabled);
        editor.apply();
    }

    public boolean showImage() {
        return sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true);
    }

    public void setShowImage(boolean show) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_SHOW_IMAGE, show);
        editor.apply();
    }

    public int getVersionCode() {
        Map<String, ?> values = sharedPreferences.getAll();
        return sharedPreferences.getInt("VersionCode", -1);
    }

    public void saveVersionCode() {
        sharedPreferences.edit().putInt("VersionCode", BuildConfig.VERSION_CODE).apply();
    }
}
