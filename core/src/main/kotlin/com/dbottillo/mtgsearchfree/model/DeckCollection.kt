package com.dbottillo.mtgsearchfree.model

class DeckCollection(
    val creatures: MutableList<MTGCard> = mutableListOf<MTGCard>(),
    val instantAndSorceries: MutableList<MTGCard> = mutableListOf<MTGCard>(),
    val other: MutableList<MTGCard> = mutableListOf<MTGCard>(),
    val lands: MutableList<MTGCard> = mutableListOf<MTGCard>(),
    val side: MutableList<MTGCard> = mutableListOf<MTGCard>()
) {

    fun addCards(newCards: List<MTGCard>): DeckCollection {
        newCards.forEach {
            when {
                it.isSideboard -> side.add(it)
                it.isLand -> lands.add(it)
                it.types.contains("Creature") -> creatures.add(it)
                it.types.contains("Instant") || it.types.contains("Sorcery") -> instantAndSorceries.add(it)
                else -> other.add(it)
            }
        }
        return this
    }

    fun size(): Int {
        return sizeOfSingleList(creatures) + sizeOfSingleList(instantAndSorceries) +
                sizeOfSingleList(other) + sizeOfSingleList(lands) +
                sizeOfSingleList(side)
    }

    fun sizeOfUniqueCards(): Int {
        return creatures.size + instantAndSorceries.size +
                other.size + lands.size + side.size
    }

    fun numberOfUniqueCards(): Int {
        return creatures.size + instantAndSorceries.size + other.size + lands.size + side.size
    }

    fun numberOfCardsWithoutSideboard(): Int {
        return size() - numberOfCardsInSideboard()
    }

    fun numberOfCardsInSideboard(): Int {
        return sizeOfSingleList(side)
    }

    fun numberOfUniqueCardsInSideboard(): Int {
        return side.size
    }

    fun getNumberOfUniqueCreatures(): Int {
        return creatures.size
    }

    fun getNumberOfCreatures(): Int {
        return sizeOfSingleList(creatures)
    }

    fun getNumberOfUniqueInstantAndSorceries(): Int {
        return instantAndSorceries.size
    }

    fun getNumberOfInstantAndSorceries(): Int {
        return sizeOfSingleList(instantAndSorceries)
    }

    fun getNumberOfUniqueLands(): Int {
        return lands.size
    }

    fun getNumberOfLands(): Int {
        return sizeOfSingleList(lands)
    }

    fun getNumberOfUniqueOther(): Int {
        return other.size
    }

    fun getNumberOfOther(): Int {
        return sizeOfSingleList(other)
    }

    fun allCards(): List<MTGCard> {
        val newList = mutableListOf<MTGCard>()
        newList.addAll(creatures)
        newList.addAll(instantAndSorceries)
        newList.addAll(other)
        newList.addAll(lands)
        newList.addAll(side)
        return newList
    }

    fun toCardsCollection(): CardsCollection {
        return CardsCollection(list = allCards(), filter = null, isDeck = true)
    }

    internal fun sizeOfSingleList(list: List<MTGCard>): Int {
        var total = 0
        list.forEach { total += it.quantity }
        return total
    }
}
