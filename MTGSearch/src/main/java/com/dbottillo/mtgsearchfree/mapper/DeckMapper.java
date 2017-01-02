package com.dbottillo.mtgsearchfree.mapper;

import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

public class DeckMapper {

    public DeckBucket map(List<MTGCard> mtgCards) {
        DeckBucket bucket = new DeckBucket();
        bucket.setCards(mtgCards);
        return bucket;
    }
}
