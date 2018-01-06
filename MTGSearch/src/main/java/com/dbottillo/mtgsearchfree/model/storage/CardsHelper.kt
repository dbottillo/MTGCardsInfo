package com.dbottillo.mtgsearchfree.model.storage

import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import java.util.*


class CardsHelper {

    fun filterCards(filter: CardFilter?, list: List<MTGCard>, useId: Boolean): List<MTGCard> {
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
        sortCards(filter.sortWUBGR, cards, useId)
        return cards
    }

    private fun sortCards(wubrgSort: Boolean, list: List<MTGCard>, useId: Boolean) {
        Collections.sort(list) { left, right ->
            if (wubrgSort) {
                if (useId) {
                    left.id.compareTo(right.id)
                } else {
                    left.compareTo(right)
                }
            } else {
                left.name.compareTo(right.name)
            }
        }
    }

    fun sortCards(filter: CardFilter, list: List<MTGCard>, useId: Boolean) {
        sortCards(filter.sortWUBGR, list, useId)
    }

}