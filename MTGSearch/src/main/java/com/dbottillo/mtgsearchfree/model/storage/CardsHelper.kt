package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import java.util.*


class CardsHelper {


    fun filterCards(filter: CardFilter?, list: List<MTGCard>): List<MTGCard> {
        if (filter == null) {
            return list
        }
        val cards = mutableListOf<MTGCard>()
        list.forEach {
            var toAdd = (it.isWhite && filter.white)
                    || (it.isBlue && filter.blue)
                    || (it.isBlack && filter.black)
                    || (it.isRed && filter.red)
                    || (it.isGreen && filter.green)
                    || (it.isArtifact && filter.artifact)
                    || (it.isLand && filter.land)
                    || (it.isEldrazi && filter.eldrazi)
            if (toAdd) {
                if ((it.isCommon && !filter.common) || (it.isUncommon && !filter.uncommon) ||
                        (it.isRare && !filter.rare) || (it.isMythicRare && !filter.mythic)) {
                    toAdd = false
                }
            }
            if (toAdd) {
                cards.add(it)
            }
        }
        sortCards(filter.sortWUBGR, cards)
        return cards
    }

    internal fun sortCards(wubrgSort: Boolean, list: List<MTGCard>) {
        Collections.sort(list) { left, right ->
            if (wubrgSort) {
                left.compareTo(right)
            } else {
                left.name.compareTo(right.name)
            }
        }
    }

    fun sortCards(filter: CardFilter, list: List<MTGCard>) {
        sortCards(filter.sortWUBGR, list)
    }

}