package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.CardFilterView;

import javax.inject.Inject;

public class CardFilterPresenterImpl implements CardFilterPresenter, Runner.RxWrapperListener<CardFilter> {

    private final CardFilterInteractor interactor;
    private final Runner<CardFilter> wrapper;
    private final MemoryStorage memoryStorage;
    private final Logger logger;
    private CardFilterView filterView;

    @Inject
    public CardFilterPresenterImpl(CardFilterInteractor interactor, RunnerFactory runnerFactory,
                                   MemoryStorage memoryStorage, Logger logger) {
        this.logger = logger;
        this.interactor = interactor;
        this.wrapper = runnerFactory.simple();
        this.memoryStorage = memoryStorage;
        logger.d("created");
    }

    @Override
    public void init(CardFilterView view) {
        logger.d();
        filterView = view;
    }

    public void loadFilter() {
        logger.d();
        if (memoryStorage.getFilter() != null) {
            logger.d("filters already in memory, will just return");
            filterLoaded();
        } else {
            logger.d("obs created and now subscribe");
            wrapper.run(interactor.load(), this);
        }
    }

    @Override
    public void onNext(CardFilter cardFilter) {
        logger.d();
        memoryStorage.setFilter(cardFilter);
        filterLoaded();
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onCompleted() {

    }

    private void filterLoaded() {
        logger.d();
        filterView.filterLoaded(memoryStorage.getFilter());
    }

    public void update(CardFilter.TYPE type, boolean on) {
        logger.d("update " + type.toString() + "with: " + on);
        if (memoryStorage.getFilter() == null){
            memoryStorage.setFilter(new CardFilter());
        }
        switch (type) {
            case WHITE:
                memoryStorage.getFilter().white = on;
                break;
            case BLUE:
                memoryStorage.getFilter().blue = on;
                break;
            case RED:
                memoryStorage.getFilter().red = on;
                break;
            case BLACK:
                memoryStorage.getFilter().black = on;
                break;
            case GREEN:
                memoryStorage.getFilter().green = on;
                break;
            case LAND:
                memoryStorage.getFilter().land = on;
                break;
            case ELDRAZI:
                memoryStorage.getFilter().eldrazi = on;
                break;
            case ARTIFACT:
                memoryStorage.getFilter().artifact = on;
                break;
            case COMMON:
                memoryStorage.getFilter().common = on;
                break;
            case UNCOMMON:
                memoryStorage.getFilter().uncommon = on;
                break;
            case RARE:
                memoryStorage.getFilter().rare = on;
                break;
            case MYTHIC:
                memoryStorage.getFilter().mythic = on;
                break;
        }
        interactor.sync(memoryStorage.getFilter());
        filterLoaded();
    }

}
