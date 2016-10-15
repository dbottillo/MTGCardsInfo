package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.util.LOG;

@SuppressWarnings("checkstyle:finalclass")
public class GeneralPreferences implements GeneralData {

    private static final String DEBUG = "debug";
    private static final String CARDS_SHOW_TYPE = "cardShowType";
    private static final String TOOLTIP_MAIN_SHOWN = "tooltipMainShow";

    private SharedPreferences sharedPreferences;

    public GeneralPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("General", Context.MODE_PRIVATE);
        LOG.d("created");
    }

    public void setDebug() {
        sharedPreferences.edit().putBoolean(DEBUG, true).apply();
    }

    public boolean isDebugEnabled() {
        return BuildConfig.DEBUG || sharedPreferences.getBoolean(DEBUG, false);
    }

    public void setCardsShowTypeList() {
        LOG.d();
        sharedPreferences.edit().putString(CARDS_SHOW_TYPE, "List").apply();
    }

    public void setCardsShowTypeGrid() {
        LOG.d();
        sharedPreferences.edit().putString(CARDS_SHOW_TYPE, "Grid").apply();
    }

    public boolean isCardsShowTypeGrid() {
        LOG.d();
        return sharedPreferences.getString(CARDS_SHOW_TYPE, "Grid").equalsIgnoreCase("Grid");
    }

    public void setTooltipMainHide() {
        LOG.d();
        sharedPreferences.edit().putBoolean(TOOLTIP_MAIN_SHOWN, false).apply();
    }

    public boolean isTooltipMainToShow() {
        LOG.d();
        return sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true);
    }

    @Override
    public long getDefaultDuration() {
        return 2000;
    }

    @VisibleForTesting
    void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
