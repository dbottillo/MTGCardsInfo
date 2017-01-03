package com.dbottillo.mtgsearchfree.interactors;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;

import java.util.List;

import io.reactivex.Observable;

public interface CardsInteractor {

    Observable<List<MTGCard>> loadSet(MTGSet set);

    Observable<int[]> saveAsFavourite(MTGCard card);

    Observable<int[]> removeFromFavourite(MTGCard card);

    Observable<int[]> loadIdFav();

    Observable<List<MTGCard>> getLuckyCards(int howMany);

    Observable<List<MTGCard>> getFavourites();

    Observable<List<MTGCard>> loadDeck(Deck deck);

    Observable<List<MTGCard>> doSearch(SearchParams searchParams);

    Observable<MTGCard> loadCard(int multiverseid);

    Observable<MTGCard> loadOtherSideCard(MTGCard card);
}