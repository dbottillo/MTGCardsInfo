package com.dbottillo.mtgsearchfree.mapper

import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.DeckBucket
import com.dbottillo.mtgsearchfree.model.MTGCard

class DeckMapper {

    fun map(cardsCollection: CardsCollection): DeckBucket {
        val bucket = DeckBucket()
        bucket.cards = cardsCollection.list
        return bucket
    }

    fun order(cards: List<MTGCard>) : List<MTGCard> {
        val side = mutableListOf<MTGCard>()
        val lands = mutableListOf<MTGCard>()
        val creatures = mutableListOf<MTGCard>()
        val instant = mutableListOf<MTGCard>()
        val rest = mutableListOf<MTGCard>()
        cards.forEach {
            when{
                it.isSideboard -> side.add(it)
                it.isLand -> lands.add(it)
                it.types.contains("Creature")  -> creatures.add(it)
                it.isLand -> lands.add(it)
                it.types.contains("Instant") || it.types.contains("Sorcery") -> instant.add(it)
                else -> rest.add(it)
            }
        }
        return creatures + instant + rest + lands + side
    }
}
