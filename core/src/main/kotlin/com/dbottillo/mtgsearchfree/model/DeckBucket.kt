package com.dbottillo.mtgsearchfree.model

data class DeckBucket(
    val creatures: MutableList<MTGCard> = mutableListOf(),
    val instantAndSorceries: MutableList<MTGCard> = mutableListOf(),
    val other: MutableList<MTGCard> = mutableListOf(),
    val lands: MutableList<MTGCard> = mutableListOf(),
    val side: MutableList<MTGCard> = mutableListOf()
) {

    var cards: List<MTGCard>
        get() {
            return creatures + instantAndSorceries + other + lands + side
        }
        set(cards) {
            for (card in cards) {
                if (card.isSideboard) {
                    side.add(card)
                } else if (card.isLand) {
                    lands.add(card)
                } else if (card.types.contains("Creature")) {
                    creatures.add(card)
                } else if (card.types.contains("Instant") || card.types.contains("Sorcery")) {
                    instantAndSorceries.add(card)
                } else {
                    other.add(card)
                }
            }
        }

    val numberOfUniqueCreatures: Int
        get() = creatures.size

    val numberOfCreatures: Int
        get() = totalQuantityOfList(creatures)

    val numberOfUniqueInstantAndSorceries: Int
        get() = instantAndSorceries.size

    val numberOfInstantAndSorceries: Int
        get() = totalQuantityOfList(instantAndSorceries)

    val numberOfUniqueLands: Int
        get() = lands.size

    val numberOfLands: Int
        get() = totalQuantityOfList(lands)

    val numberOfUniqueOther: Int
        get() = other.size

    val numberOfOther: Int
        get() = totalQuantityOfList(other)

    fun numberOfCards(): Int {
        return totalQuantityOfList(cards)
    }

    fun numberOfUniqueCards(): Int {
        return creatures.size + instantAndSorceries.size + other.size + lands.size + side.size
    }

    fun numberOfCardsWithoutSideboard(): Int {
        return numberOfCards() - numberOfCardsInSideboard()
    }

    fun numberOfCardsInSideboard(): Int {
        return totalQuantityOfList(side)
    }

    fun numberOfUniqueCardsInSideboard(): Int {
        return side.size
    }

    private fun totalQuantityOfList(list: List<MTGCard>): Int {
        return list.sumBy { it.quantity }
    }
}
