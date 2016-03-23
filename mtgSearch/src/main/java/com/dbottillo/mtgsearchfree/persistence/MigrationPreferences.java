package com.dbottillo.mtgsearchfree.persistence;

import android.content.Context;
import android.content.SharedPreferences;

@Deprecated
public class MigrationPreferences {

    public static final String PREFS_NAME = "Migration";
    private static final String MIGRATION = "migration";

    public static final int MIGRATION_NOT_STARTED = -1;
    public static final int MIGRATION_IN_PROGRESS = 0;
    public static final int MIGRATION_FINISHED = 1;

    SharedPreferences sharedPreferences;

    public MigrationPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public boolean migrationNotStarted() {
        return sharedPreferences.getInt(MIGRATION, -1) == MIGRATION_NOT_STARTED;
    }

    public boolean migrationInProgress() {
        return sharedPreferences.getInt(MIGRATION, -1) == MIGRATION_IN_PROGRESS;
    }

    public void setStarted() {
        sharedPreferences.edit().putInt(MIGRATION, MIGRATION_IN_PROGRESS).apply();
    }

    public void setFinished() {
        sharedPreferences.edit().putInt(MIGRATION, MIGRATION_FINISHED).apply();
    }
}
