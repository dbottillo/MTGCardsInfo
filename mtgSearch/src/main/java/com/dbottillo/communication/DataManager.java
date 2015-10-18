package com.dbottillo.communication;

import android.content.Context;

import com.dbottillo.communication.events.ErrorEvent;
import com.dbottillo.database.CardsDatabaseHelper;

import de.greenrobot.event.EventBus;

public final class DataManager {

    private static CardsDatabaseHelper cardAdapterHelper;

    private DataManager() {

    }

    public enum TASK {
        SET_LIST(SetListOperation.class),
        SET_CARDS(SetCardsOperation.class),
        SEARCH_CARDS(SearchCardsOperation.class),
        SAVED_CARDS(SavedCardsOperation.class),
        SAVE_CARD(SaveCardOperation.class),
        UN_SAVE_CARD(UnsaveCardOperation.class),
        RANDOM_CARDS(RandomCardsOperation.class),
        PLAYERS(PlayersOperation.class);

        Class<?> operationClass;

        TASK(Class<?> operationClass) {
            this.operationClass = operationClass;
        }

        public Class<?> getOperationClass() {
            return operationClass;
        }
    }

    public static void with(Context ctx) {
        cardAdapterHelper = new CardsDatabaseHelper(ctx);
    }

    public static void execute(final TASK task, final Object... params) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Operation operation = (Operation) task.getOperationClass().newInstance();
                    operation.execute(cardAdapterHelper, params);
                } catch (InstantiationException e) {
                    EventBus.getDefault().post(new ErrorEvent(e.getLocalizedMessage()));
                } catch (IllegalAccessException e) {
                    EventBus.getDefault().post(new ErrorEvent(e.getLocalizedMessage()));
                }
            }
        };
        new Thread(runnable).start();
    }
}
