package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.view.CardFilterView;

import javax.inject.Inject;

public class CardFilterPresenterImpl implements CardFilterPresenter, RxWrapper.RxWrapperListener<CardFilter> {

    CardFilterInteractor interactor;
    private CardFilterView filterView;
    private RxWrapper<CardFilter> wrapper;
    private MemoryStorage memoryStorage;

    @Inject
    public CardFilterPresenterImpl(CardFilterInteractor interactor, RxWrapperFactory rxWrapperFactory,
                                   MemoryStorage memoryStorage) {
        LOG.d("created");
        this.interactor = interactor;
        this.wrapper = rxWrapperFactory.singleWrapper();
        this.memoryStorage = memoryStorage;
    }

    @Override
    public void init(CardFilterView view) {
        LOG.d();
        filterView = view;
    }

    public void loadFilter() {
        LOG.d();
        if (memoryStorage.getFilter() != null) {
            LOG.d("filters already in memory, will just return");
            filterLoaded();
        } else {
            LOG.d("obs created and now subscribe");
            wrapper.run(interactor.load(), this);
        }
    }

    @Override
    public void onNext(CardFilter cardFilter) {
        LOG.d();
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
        LOG.d();
        filterView.filterLoaded(memoryStorage.getFilter());
    }

    public void update(CardFilter.TYPE type, boolean on) {
        LOG.d("update " + type.toString() + "with: " + on);
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
