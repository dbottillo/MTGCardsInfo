package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;
import android.content.SharedPreferences;

public final class GeneralPreferences {

    public static final String PREFS_NAME = "General";

    private static final String DEBUG = "debug";

    SharedPreferences sharedPreferences;

    private static GeneralPreferences instance;

    private GeneralPreferences() {

    }

    public static GeneralPreferences with(Context context) {
        if (instance == null) {
            instance = new GeneralPreferences(context);
        }
        return instance;
    }

    private GeneralPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setDebug() {
        sharedPreferences.edit().putBoolean(DEBUG, true).apply();
    }

    public boolean isDebugEnabled() {
        return sharedPreferences.getBoolean(DEBUG, false);
    }
}
