package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.ArrayList;

import rx.Observable;

public interface CardsInteractor {

    Observable<ArrayList<MTGCard>> load(MTGSet set);

    Observable<int[]> saveAsFavourite(MTGCard card);

    Observable<int[]> removeFromFavourite(MTGCard card);

    Observable<int[]> loadIdFav();

    Observable<ArrayList<MTGCard>> getLuckyCards(int howMany);

    Observable<ArrayList<MTGCard>> getFavourites();

    Observable<ArrayList<MTGCard>> loadDeck(Deck deck);
}