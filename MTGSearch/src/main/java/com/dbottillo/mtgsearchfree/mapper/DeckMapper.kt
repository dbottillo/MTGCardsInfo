package com.dbottillo.mtgsearchfree.mapper

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.DeckBucket
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard

interface DeckMapper {
    fun order(cards: List<MTGCard>) : List<MTGCard>
}
