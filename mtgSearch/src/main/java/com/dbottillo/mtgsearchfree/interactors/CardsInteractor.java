package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

import rx.Observable;

public interface CardsInteractor {

    Observable<ArrayList<MTGCard>> load(MTGSet set);

    Observable<int[]> saveAsFavourite(MTGCard card);

    Observable<int[]> removeFromFavourite(MTGCard card);

    Observable<int[]> loadIdFav();

    Observable<ArrayList<MTGCard>> getLuckyCards(int howMany);
}