package com.dbottillo.mtgsearchfree.model

import android.content.Context
import android.support.annotation.VisibleForTesting
import com.dbottillo.mtgsearchfree.R

data class MTGCard(var id: Int = 0,
                   var name: String = "",
                   var type: String = "",
                   val types: MutableList<String> = mutableListOf(),
                   val subTypes: MutableList<String> = mutableListOf(),
                   var colors: MutableList<Int> = mutableListOf(),
                   var cmc: Int = 0,
                   var rarity: String = "",
                   var power: String = "",
                   var toughness: String = "",
                   var manaCost: String = "",
                   var text: String = "",
                   var isMultiColor: Boolean = false,
                   var isLand: Boolean = false,
                   var isArtifact: Boolean = false,
                   var multiVerseId: Int = 0,
                   var set: MTGSet? = null,
                   var quantity: Int = 1,
                   var isSideboard: Boolean = false,
                   var layout: String = "normal",
                   var number: String? = null,
                   val rulings: MutableList<String> = mutableListOf(),
                   var names: List<String> = listOf(),
                   var superTypes: List<String> = listOf(),
                   var artist: String = "",
                   var flavor: String? = null,
                   var loyalty: Int = 0,
                   var printings: List<String> = listOf(),
                   var originalText: String = "",
                   var mciNumber: String? = null,
                   var colorsIdentity: List<String> = listOf(),
                   var legalities: MutableList<Legality> = mutableListOf()) : Comparable<MTGCard> {

    constructor(onlyId: Int) : this(id = onlyId)

    constructor(onlyId: Int, multiVerseId: Int) : this(id = onlyId, multiVerseId = multiVerseId)

    fun setCardName(name: String) {
        this.name = name
    }

    fun addType(type: String) {
        this.types.add(type)
    }

    fun addSubType(subType: String) {
        this.subTypes.add(subType)
    }

    fun addColor(color: String) {
        colors.add(CardProperties.COLOR.getNumberFromString(color))
    }

    val isEldrazi: Boolean
        get() = !isMultiColor && !isLand && !isArtifact && colors.size == 0

    fun belongsTo(set: MTGSet) {
        this.set = set
    }

    override fun toString(): String {
        return "MTGCard: [$id,$name,$multiVerseId,$colors,$rarity,$quantity]"
    }

    fun addRuling(rule: String) {
        this.rulings.add(rule)
    }

    fun addLegality(legality: Legality) {
        legalities.add(legality)
    }

    val isCommon: Boolean
        get() = rarity.equals(CardFilter.FILTER_COMMON, ignoreCase = true)

    val isUncommon: Boolean
        get() = rarity.equals(CardFilter.FILTER_UNCOMMON, ignoreCase = true)

    val isRare: Boolean
        get() = rarity.equals(CardFilter.FILTER_RARE, ignoreCase = true)

    val isMythicRare: Boolean
        get() = rarity.equals(CardFilter.FILTER_MYHTIC, ignoreCase = true)

    val image: String?
        get() = if (number != null && set != null && number!!.isNotEmpty()
                && !types.contains("Plane")
                && set?.code?.toUpperCase() != "6ED"
                && set?.code?.toUpperCase() != "DDT"
                && set?.code?.toUpperCase() != "IMA" ) {
            "https://magiccards.info/scans/en/" + set?.magicCardsInfoCode + "/" + mciNumberOrMultiverseId + ".jpg"
        } else imageFromGatherer

    val imageFromGatherer: String?
        get() = if (multiVerseId > 0) {
            "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=$multiVerseId&type=card"
        } else null

    private val mciNumberOrMultiverseId: String?
        get() = if (mciNumber == null || mciNumber!!.length == 0) {
            number
        } else mciNumber

    override fun compareTo(other: MTGCard): Int {
        if (isLand && other.isLand) {
            return 0
        }
        if (!isLand && other.isLand) {
            return -1
        }
        if (isLand) {
            return 1
        }
        if (isArtifact && other.isArtifact) {
            return 0
        }
        if (!isArtifact && other.isArtifact) {
            return -1
        }
        if (isArtifact) {
            return 1
        }
        if (isMultiColor && other.isMultiColor) {
            return 0
        }
        if (!isMultiColor && other.isMultiColor) {
            return -1
        }
        if (isMultiColor) {
            return 1
        }

        if (other.singleColor == this.singleColor) {
            return 0
        }
        return if (singleColor < other.singleColor) {
            -1
        } else 1
    }

    val singleColor: Int
        @VisibleForTesting
        get() = if (isMultiColor || colors.isEmpty()) {
            -1
        } else colors[0]

    fun getMtgColor(context: Context): Int {
        var mtgColor = context.resources.getColor(R.color.mtg_other)
        if (isMultiColor) {
            mtgColor = context.resources.getColor(R.color.mtg_multi)
        } else if (colors.contains(CardProperties.COLOR.WHITE.value)) {
            mtgColor = context.resources.getColor(R.color.mtg_white)
        } else if (colors.contains(CardProperties.COLOR.BLUE.value)) {
            mtgColor = context.resources.getColor(R.color.mtg_blue)
        } else if (colors.contains(CardProperties.COLOR.BLACK.value)) {
            mtgColor = context.resources.getColor(R.color.mtg_black)
        } else if (colors.contains(CardProperties.COLOR.RED.value)) {
            mtgColor = context.resources.getColor(R.color.mtg_red)
        } else if (colors.contains(CardProperties.COLOR.GREEN.value)) {
            mtgColor = context.resources.getColor(R.color.mtg_green)
        }
        return mtgColor
    }

    val isWhite: Boolean
        get() = manaCost.contains("W")

    val isBlue: Boolean
        get() = manaCost.contains("U")

    val isBlack: Boolean
        get() = manaCost.contains("B")

    val isRed: Boolean
        get() = manaCost.contains("R")

    val isGreen: Boolean
        get() = manaCost.contains("G")

    fun hasNoColor(): Boolean {
        return !manaCost.matches(".*[WUBRG].*".toRegex())
    }

    val isDoubleFaced: Boolean
        get() = layout.equals("double-faced", ignoreCase = true)

    override fun equals(other: Any?): Boolean {
        return when {
            other !is MTGCard -> false
            multiVerseId > 0 && other.multiVerseId == multiVerseId -> true
            name.isNotEmpty() && other.name == name -> true
            else -> false
        }
    }

}

class Legality(val format: String, val legality: String)