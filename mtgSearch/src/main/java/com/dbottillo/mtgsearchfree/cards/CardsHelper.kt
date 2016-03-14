package com.dbottillo.mtgsearchfree.cards

import com.dbottillo.mtgsearchfree.helper.FilterHelper
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.search.SearchParams

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

object CardsHelper {

    fun filterCards(cardFilter: CardFilter, input: ArrayList<MTGCard>, output: ArrayList<MTGCard>) {
        filterCards(cardFilter, null, input, output)
    }

    fun filterCards(cardFilter: CardFilter?, searchParams: SearchParams?, input: ArrayList<MTGCard>, output: ArrayList<MTGCard>) {
        if (cardFilter == null) {
            return
        }
        for (card in input) {
            var toAdd = false
            if (searchParams == null) {
                if (card.isWhite && cardFilter.white) {
                    toAdd = true
                }
                if (card.isBlue && cardFilter.blue) {
                    toAdd = true
                }
                if (card.isBlack && cardFilter.black) {
                    toAdd = true
                }
                if (card.isRed && cardFilter.red) {
                    toAdd = true
                }
                if (card.isGreen && cardFilter.green) {
                    toAdd = true
                }
                if (card.isLand && cardFilter.land) {
                    toAdd = true
                }
                if (card.isArtifact && cardFilter.artifact) {
                    toAdd = true
                }
                if (card.isEldrazi && cardFilter.eldrazi) {
                    toAdd = true
                }
                if (toAdd && card.rarity.equals(FilterHelper.FILTER_COMMON, ignoreCase = true)
                        && !cardFilter.common) {
                    toAdd = false
                }
                if (toAdd && card.rarity.equals(FilterHelper.FILTER_UNCOMMON, ignoreCase = true)
                        && !cardFilter.uncommon) {
                    toAdd = false
                }
                if (toAdd && card.rarity.equals(FilterHelper.FILTER_RARE, ignoreCase = true)
                        && !cardFilter.rare) {
                    toAdd = false
                }
                if (toAdd && card.rarity.equals(FilterHelper.FILTER_MYHTIC, ignoreCase = true)
                        && !cardFilter.mythic) {
                    toAdd = false
                }
            } else {
                // for search, filter don't apply
                toAdd = true
            }

            if (toAdd) {
                output.add(card)
            }
        }
    }

    fun sortCards(wubrgSort: Boolean, cards: ArrayList<MTGCard>) {
        if (wubrgSort) {
            Collections.sort(cards) { o1, o2 ->
                val card = o1 as MTGCard
                val card2 = o2 as MTGCard
                card.compareTo(card2)
            }
        } else {
            Collections.sort(cards) { o1, o2 ->
                val card = o1 as MTGCard
                val card2 = o2 as MTGCard
                card.name.compareTo(card2.name)
            }
        }
    }

}
