package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.CardView;

import javax.inject.Inject;

public class CardPresenterImpl implements CardPresenter {

    private final CardsInteractor interactor;
    private CardView cardView;
    private final Logger logger;

    private final Runner<MTGCard> cardWrapper;

    @Inject
    public CardPresenterImpl(CardsInteractor interactor, Logger logger, RunnerFactory runnerFactory) {
        this.logger = logger;
        logger.d("created");
        this.interactor = interactor;
        this.cardWrapper = runnerFactory.simple();
    }

    @Override
    public void loadOtherSideCard(MTGCard card) {
        cardWrapper.run(interactor.loadOtherSideCard(card), new Runner.RxWrapperListener<MTGCard>() {
            @Override
            public void onNext(MTGCard card) {
                logger.d();
                cardView.otherSideCardLoaded(card);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void init(CardView cardView) {
        this.cardView = cardView;
    }

}