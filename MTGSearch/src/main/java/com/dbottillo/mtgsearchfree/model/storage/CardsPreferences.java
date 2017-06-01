package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.CardFilter;

public interface CardsPreferences {

    CardFilter load();

    void sync(CardFilter filter);

    void saveSetPosition(int position);

    int getSetPosition();

    boolean showPoison();

    boolean twoHGEnabled();

    boolean screenOn();

    void showPoison(boolean show);

    void setScreenOn(boolean on);

    void setTwoHGEnabled(boolean enabled);

    boolean showImage();

    void setShowImage(boolean show);

    int getVersionCode();

    void saveVersionCode();
}
