package com.dbottillo.helper;

import android.util.Log;

import com.dbottillo.BuildConfig;

public final class LOG {

    private LOG() {

    }

    private static final String TAG = "MTG_TAG";

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }
}
