package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.dbottillo.mtgsearchfree.BuildConfig;
import com.dbottillo.mtgsearchfree.util.LOG;

@SuppressWarnings("checkstyle:finalclass")
public class GeneralPreferencesForTest implements GeneralData {

    boolean debug = false;
    boolean typeList = false;

    public GeneralPreferencesForTest() {

    }

    @Override
    public void setDebug() {
        debug = true;
    }

    @Override
    public boolean isDebugEnabled() {
        return debug;
    }

    @Override
    public void setCardsShowTypeList() {
        typeList = true;
    }

    @Override
    public void setCardsShowTypeGrid() {
        typeList = false;
    }

    @Override
    public boolean isCardsShowTypeGrid() {
        LOG.e("here!");
        return !typeList;
    }

    @Override
    public void setTooltipMainHide() {

    }

    @Override
    public boolean isTooltipMainToShow() {
        return false;
    }

    @Override
    public long getDefaultDuration() {
        return 50;
    }
}
