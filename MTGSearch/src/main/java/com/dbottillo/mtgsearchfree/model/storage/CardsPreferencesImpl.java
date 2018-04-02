package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.ui.BasicFragment;
import com.dbottillo.mtgsearchfree.util.LOG;

public class CardsPreferencesImpl implements CardsPreferences {

    private final static String PREFS_NAME = "Filter";
    private SharedPreferences sharedPreferences;

    private final static String WHITE = "White";
    private final static String BLUE = "Blue";
    private final static String BLACK = "Black";
    private final static String RED = "Red";
    private final static String GREEN = "Green";

    private final static String ARTIFACT = "Artifact";
    private final static String LAND = "Land";
    private final static String ELDRAZI = "Eldrazi";

    private final static String COMMON = "Common";
    private final static String UNCOMMON = "Uncommon";
    private final static String RARE = "Rare";
    private final static String MYTHIC = "Mythic Rare";

    public CardsPreferencesImpl(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    @Override
    public CardFilter load() {
        LOG.INSTANCE.d("");
        CardFilter res = new CardFilter();
        res.setWhite(sharedPreferences.getBoolean(WHITE, true));
        res.setBlue(sharedPreferences.getBoolean(BLUE, true));
        res.setBlack(sharedPreferences.getBoolean(BLACK, true));
        res.setRed(sharedPreferences.getBoolean(RED, true));
        res.setGreen(sharedPreferences.getBoolean(GREEN, true));

        res.setArtifact(sharedPreferences.getBoolean(ARTIFACT, true));
        res.setLand(sharedPreferences.getBoolean(LAND, true));
        res.setEldrazi(sharedPreferences.getBoolean(ELDRAZI, true));

        res.setCommon(sharedPreferences.getBoolean(COMMON, true));
        res.setUncommon(sharedPreferences.getBoolean(UNCOMMON, true));
        res.setRare(sharedPreferences.getBoolean(RARE, true));
        res.setMythic(sharedPreferences.getBoolean(MYTHIC, true));

        res.setSortWUBGR(sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true));

        return res;
    }

    @Override
    public void sync(CardFilter filter) {
        LOG.INSTANCE.d("");
        sharedPreferences.edit()
                .putBoolean(WHITE, filter.getWhite())
                .putBoolean(BLUE, filter.getBlue())
                .putBoolean(BLACK, filter.getBlack())
                .putBoolean(RED, filter.getRed())
                .putBoolean(GREEN, filter.getGreen())
                .putBoolean(ARTIFACT, filter.getArtifact())
                .putBoolean(LAND, filter.getLand())
                .putBoolean(ELDRAZI, filter.getEldrazi())
                .putBoolean(COMMON, filter.getCommon())
                .putBoolean(UNCOMMON, filter.getUncommon())
                .putBoolean(RARE, filter.getRare())
                .putBoolean(MYTHIC, filter.getMythic())
                .putBoolean(BasicFragment.PREF_SORT_WUBRG, filter.getSortWUBGR())
                .apply();
    }

    @Override
    public void saveSetPosition(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("setPosition", position);
        editor.apply();
    }

    @Override
    public int getSetPosition() {
        return sharedPreferences.getInt("setPosition", 0);
    }

    @Override
    public boolean showPoison() {
        return sharedPreferences.getBoolean("poison", false);
    }

    @Override
    public boolean twoHGEnabled() {
        return sharedPreferences.getBoolean(BasicFragment.PREF_TWO_HG_ENABLED, false);
    }

    @Override
    public boolean screenOn() {
        return sharedPreferences.getBoolean(BasicFragment.PREF_SCREEN_ON, false);
    }

    @Override
    public void showPoison(boolean show) {
        sharedPreferences.edit().putBoolean("poison", show).apply();
    }

    @Override
    public void setScreenOn(boolean on) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_SCREEN_ON, on);
        editor.apply();
    }

    @Override
    public void setTwoHGEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_TWO_HG_ENABLED, enabled);
        editor.apply();
    }

    @Override
    public boolean showImage() {
        return sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true);
    }

    @Override
    public void setShowImage(boolean show) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BasicFragment.PREF_SHOW_IMAGE, show);
        editor.apply();
    }

    @Override
    public int getVersionCode() {
        return sharedPreferences.getInt("VersionCode", -1);
    }

    @Override
    public void saveVersionCode() {
        sharedPreferences.edit().putInt("VersionCode", BuildConfig.VERSION_CODE).apply();
    }

    @VisibleForTesting
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
