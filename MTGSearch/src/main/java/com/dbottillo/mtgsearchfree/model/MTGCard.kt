package com.dbottillo.mtgsearchfree.model

import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.content.ContextCompat
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
                   var colorsIdentity: List<String>? = null,
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
                && set?.code?.toUpperCase() != "6ED") {
            "https://magiccards.info/scans/en/" + set?.magicCardsInfoCode + "/" + mciNumberOrMultiverseId + ".jpg"
        } else imageFromGatherer

    val imageFromGatherer: String?
        get() = if (multiVerseId > 0) {
            "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=$multiVerseId&type=card"
        } else null

    private val mciNumberOrMultiverseId: String?
        get() = if (mciNumber.isNullOrEmpty()) {
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
        if (isColorlessArtifact && other.isColorlessArtifact) {
            return 0
        }
        if (!isColorlessArtifact && other.isColorlessArtifact) {
            return -1
        }
        if (isColorlessArtifact) {
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
        } else {
            1
        }
    }

    private val isColorlessArtifact: Boolean
        get() = colorsIdentity?.let { it.isEmpty() && isArtifact } ?: isArtifact

    val singleColor: Int
        @VisibleForTesting
        get() =
            when {
                isMultiColor -> -1
                colorsIdentity != null -> colorsIdentity!![0].toColorInt()
                colors.isNotEmpty() -> colors[0]
                else -> -1
            }

    fun getMtgColor(context: Context): Int {
        return ContextCompat.getColor(context,
                when {
                    isMultiColor -> R.color.mtg_multi
                    colors.contains(CardProperties.COLOR.WHITE.value) -> R.color.mtg_white
                    colors.contains(CardProperties.COLOR.BLUE.value) -> R.color.mtg_blue
                    colors.contains(CardProperties.COLOR.BLACK.value) -> R.color.mtg_black
                    colors.contains(CardProperties.COLOR.RED.value) -> R.color.mtg_red
                    colors.contains(CardProperties.COLOR.GREEN.value) -> R.color.mtg_green
                    else -> R.color.mtg_other
                })
    }

    val isWhite: Boolean
        get() = manaCost.contains("W") || colorsIdentity?.contains("W") ?: false

    val isBlue: Boolean
        get() = manaCost.contains("U") || colorsIdentity?.contains("U") ?: false

    val isBlack: Boolean
        get() = manaCost.contains("B") || colorsIdentity?.contains("B") ?: false

    val isRed: Boolean
        get() = manaCost.contains("R") || colorsIdentity?.contains("R") ?: false

    val isGreen: Boolean
        get() = manaCost.contains("G") || colorsIdentity?.contains("G") ?: false

    fun hasNoColor(): Boolean {
        return !manaCost.matches(".*[WUBRG].*".toRegex()) && colorsIdentity?.isEmpty() ?: false
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

fun String.toColorInt(): Int {
    return when (this) {
        "W" -> 0
        "U" -> 1
        "B" -> 2
        "R" -> 3
        "G" -> 4
        else -> 1
    }
}