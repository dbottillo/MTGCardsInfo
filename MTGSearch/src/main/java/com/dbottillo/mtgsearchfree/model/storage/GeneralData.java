package com.dbottillo.mtgsearchfree.model.storage;

public interface GeneralData {

    void setDebug();

    boolean isDebugEnabled();

    void setCardsShowTypeList();

    void setCardsShowTypeGrid();

    boolean isCardsShowTypeGrid();

    void setTooltipMainHide();

    boolean isTooltipMainToShow();

    long getDefaultDuration();

    boolean isFreshInstall();

    boolean cardMigrationRequired();

    void markCardMigrationStarted();
}
