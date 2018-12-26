package com.dbottillo.mtgsearchfree.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard

class CardsHelper {

    fun filterAndSortMultipleSets(filter: CardFilter, list: List<MTGCard>): List<MTGCard> {
        return list.filterWith(filter).sortMultipleSetsBy(filter.sortSetNumber)
    }

    fun filterAndSortSet(filter: CardFilter, list: List<MTGCard>): List<MTGCard> {
        return list.filterWith(filter).sortSingleSet(filter.sortSetNumber)
    }
}

private fun List<MTGCard>.filterWith(filter: CardFilter): List<MTGCard> {
    val cards = mutableListOf<MTGCard>()
    forEach {
        val skip = (it.isWhite && !filter.white) ||
                (it.isBlue && !filter.blue) ||
                (it.isBlack && !filter.black) ||
                (it.isRed && !filter.red) ||
                (it.isGreen && !filter.green) ||
                (it.isArtifact && !filter.artifact) ||
                (it.isLand && !filter.land) ||
                (it.isEldrazi && !filter.eldrazi) ||
                (it.isCommon && !filter.common) ||
                (it.isUncommon && !filter.uncommon) ||
                (it.isRare && !filter.rare) ||
                (it.isMythicRare && !filter.mythic)
        if (!skip) {
            cards.add(it)
        }
    }
    return cards
}

private fun List<MTGCard>.sortSingleSet(sortSetNumber: Boolean): List<MTGCard> {
    val regex = Regex("[^\\d.]")
    if (sortSetNumber) {
        return this.sortedBy { regex.replace(it.number, "").ifEmpty { "0" }.toInt() }
    }
    return this.sortedBy { it.name }
}

private fun List<MTGCard>.sortMultipleSetsBy(sortSetNumber: Boolean): List<MTGCard> {
    val regex = Regex("[^\\d.]")
    if (sortSetNumber) {
        val cards = mutableListOf<MTGCard>()
        val setMap = this.groupBy { it.set }.toSortedMap(compareBy { it?.id })
        setMap.forEach { mapEntry ->
            cards.addAll(mapEntry.value.sortedBy { regex.replace(it.number, "").ifEmpty { "0" }.toInt() })
        }
        return cards
    }
    return this.sortedBy { it.name }
}