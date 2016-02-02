package com.dbottillo.mtgsearchfree.communication;

import android.content.Context;

import com.dbottillo.mtgsearchfree.communication.events.ErrorEvent;
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;

import de.greenrobot.event.EventBus;

public final class DataManager {

    private static MTGDatabaseHelper cardAdapterHelper;
    private static CardsInfoDbHelper cardsInfoDbHelper;

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
        PLAYERS(PlayersOperation.class),
        SAVE_PLAYER(SavePlayerOperation.class),
        REMOVE_PLAYER(RemovePlayerOperation.class),
        EDIT_DECK(EditDeckOperation.class),
        EDIT_DECK_NAME(EditDeckNameOperation.class);

        Class<?> operationClass;

        TASK(Class<?> operationClass) {
            this.operationClass = operationClass;
        }

        public Class<?> getOperationClass() {
            return operationClass;
        }
    }

    public static void with(Context ctx) {
        cardAdapterHelper = new MTGDatabaseHelper(ctx);
        cardsInfoDbHelper = CardsInfoDbHelper.getInstance(ctx);
    }

    public static void execute(final TASK task, final Object... params) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Operation operation = (Operation) task.getOperationClass().newInstance();
                    operation.execute(cardAdapterHelper, cardsInfoDbHelper, params);
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
