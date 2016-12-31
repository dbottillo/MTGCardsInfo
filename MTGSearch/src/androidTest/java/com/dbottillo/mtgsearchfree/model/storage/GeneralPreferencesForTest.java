package com.dbottillo.mtgsearchfree.model.storage;

@SuppressWarnings("checkstyle:finalclass")
public class GeneralPreferencesForTest implements GeneralData {

    private boolean debug = false;
    private boolean typeList = false;

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
        return 0;
    }

    @Override
    public boolean isFreshInstall() {
        return true;
    }
}
