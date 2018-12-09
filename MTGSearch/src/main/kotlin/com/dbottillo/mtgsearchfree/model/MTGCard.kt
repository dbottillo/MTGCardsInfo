package com.dbottillo.mtgsearchfree.model

import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.content.ContextCompat
import com.dbottillo.mtgsearchfree.R

data class MTGCard(
    var id: Int = 0,
    var uuid: String = "",
    var name: String = "",
    var type: String = "",
    val types: MutableList<String> = mutableListOf(),
    val subTypes: MutableList<String> = mutableListOf(),
    var colors: MutableList<Int> = mutableListOf(),
    var cmc: Int = 0,
    var rarity: Rarity = Rarity.COMMON,
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
    var colorsIdentity: List<String>? = null,
    var legalities: MutableList<Legality> = mutableListOf()
) : Comparable<MTGCard> {

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
        colors.add(color.toColorInt())
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
        get() = rarity == Rarity.COMMON

    val isUncommon: Boolean
        get() = rarity == Rarity.UNCOMMON

    val isRare: Boolean
        get() = rarity == Rarity.RARE

    val isMythicRare: Boolean
        get() = rarity == Rarity.MYTHIC

    val displayRarity: Int
        get() = when (rarity) {
            Rarity.COMMON -> R.string.search_common
            Rarity.UNCOMMON -> R.string.search_uncommon
            Rarity.RARE -> R.string.search_rare
            Rarity.MYTHIC -> R.string.search_mythic
        }

    val rarityColor: Int
        get() = when (rarity) {
            Rarity.COMMON -> R.color.uncommon
            Rarity.UNCOMMON -> R.color.uncommon
            Rarity.RARE -> R.color.rare
            Rarity.MYTHIC -> R.color.mythic
        }

    val gathererImage
        get() = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=$multiVerseId&type=card"

    val scryfallImage
        get() = if (uuid.isNotEmpty()) {
            "https://api.scryfall.com/cards/$uuid?format=image"
        } else gathererImage

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
                colorsIdentity?.isNotEmpty()
                        ?: false -> colorsIdentity!![0].fromIdentityColorToInt()
                colors.isNotEmpty() -> colors[0]
                else -> -1
            }

    fun getMtgColor(context: Context): Int {
        return ContextCompat.getColor(context,
                when {
                    isMultiColor -> R.color.mtg_multi
                    colors.contains(0) -> R.color.mtg_white
                    colors.contains(1) -> R.color.mtg_blue
                    colors.contains(2) -> R.color.mtg_black
                    colors.contains(3) -> R.color.mtg_red
                    colors.contains(4) -> R.color.mtg_green
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

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + types.hashCode()
        result = 31 * result + subTypes.hashCode()
        result = 31 * result + colors.hashCode()
        result = 31 * result + cmc
        result = 31 * result + rarity.hashCode()
        result = 31 * result + power.hashCode()
        result = 31 * result + toughness.hashCode()
        result = 31 * result + manaCost.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + isMultiColor.hashCode()
        result = 31 * result + isLand.hashCode()
        result = 31 * result + isArtifact.hashCode()
        result = 31 * result + multiVerseId
        result = 31 * result + (set?.hashCode() ?: 0)
        result = 31 * result + quantity
        result = 31 * result + isSideboard.hashCode()
        result = 31 * result + layout.hashCode()
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + rulings.hashCode()
        result = 31 * result + names.hashCode()
        result = 31 * result + superTypes.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (flavor?.hashCode() ?: 0)
        result = 31 * result + loyalty
        result = 31 * result + printings.hashCode()
        result = 31 * result + originalText.hashCode()
        result = 31 * result + (colorsIdentity?.hashCode() ?: 0)
        result = 31 * result + legalities.hashCode()
        return result
    }
}

class Legality(val format: String, val legality: String)

fun String.fromIdentityColorToInt(): Int {
    return when (this) {
        "W" -> 0
        "U" -> 1
        "B" -> 2
        "R" -> 3
        "G" -> 4
        else -> -1
    }
}

fun String.toColorInt(): Int {
    return when (this) {
        "White" -> 0
        "Blue" -> 1
        "Black" -> 2
        "Red" -> 3
        "Green" -> 4
        else -> -1
    }
}

fun Int.toColor(): String? {
    return when (this) {
        0 -> "White"
        1 -> "Blue"
        2 -> "Black"
        3 -> "Red"
        4 -> "Green"
        else -> null
    }
}