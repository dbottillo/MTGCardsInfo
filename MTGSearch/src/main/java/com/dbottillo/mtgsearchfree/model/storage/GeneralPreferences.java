package com.dbottillo.mtgsearchfree.model.storage;

import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.util.AppInfo;

@SuppressWarnings("checkstyle:finalclass")
public class GeneralPreferences implements GeneralData {

    static final String DEBUG = "debug";
    static final String CARDS_SHOW_TYPE = "cardShowType";
    static final String TOOLTIP_MAIN_SHOWN = "tooltipMainShow";
    static final String CARD_MIGRATION_REQUIRED = "cardMigrationRequired";

    private SharedPreferences sharedPreferences;
    private final AppInfo appInfo;

    public GeneralPreferences(SharedPreferences sharedPreferences, AppInfo appInfo) {
        this.appInfo = appInfo;
        this.sharedPreferences = sharedPreferences;
    }

    public void setDebug() {
        sharedPreferences.edit().putBoolean(DEBUG, true).apply();
    }

    public boolean isDebugEnabled() {
        return BuildConfig.DEBUG || sharedPreferences.getBoolean(DEBUG, false);
    }

    public void setCardsShowTypeList() {
        sharedPreferences.edit().putString(CARDS_SHOW_TYPE, "List").apply();
    }

    public void setCardsShowTypeGrid() {
        sharedPreferences.edit().putString(CARDS_SHOW_TYPE, "Grid").apply();
    }

    public boolean isCardsShowTypeGrid() {
        return sharedPreferences.getString(CARDS_SHOW_TYPE, "Grid").equalsIgnoreCase("Grid");
    }

    public void setTooltipMainHide() {
        sharedPreferences.edit().putBoolean(TOOLTIP_MAIN_SHOWN, false).apply();
    }

    public boolean isTooltipMainToShow() {
        return !isFreshInstall() && sharedPreferences.getBoolean(TOOLTIP_MAIN_SHOWN, true);
    }

    @Override
    public long getDefaultDuration() {
        return 200;
    }

    @Override
    public boolean isFreshInstall() {
        long firstInstallTime = appInfo.getFirstInstallTime();
        long lastUpdateTime = appInfo.getLastUpdateTime();
        return firstInstallTime == lastUpdateTime;
    }

    @Override
    public boolean cardMigrationRequired() {
        return false;
    }

    @Override
    public void markCardMigrationStarted() {
        sharedPreferences.edit().putBoolean(CARD_MIGRATION_REQUIRED, false).apply();
    }

}
