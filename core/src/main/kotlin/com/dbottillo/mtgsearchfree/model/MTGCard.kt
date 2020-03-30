package com.dbottillo.mtgsearchfree.model

import android.content.Context
import androidx.core.content.ContextCompat
import com.dbottillo.mtgsearchfree.core.R

data class MTGCard(
    var id: Int = 0,
    var uuid: String = "",
    var scryfallId: String = "",
    var tcgplayerProductId: Int = 0,
    var tcgplayerPurchaseUrl: String = "",
    var name: String = "",
    var type: String = "",
    val types: MutableList<String> = mutableListOf(),
    val subTypes: MutableList<String> = mutableListOf(),
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
    var number: String = "",
    val rulings: MutableList<String> = mutableListOf(),
    var names: List<String> = listOf(),
    var superTypes: List<String> = listOf(),
    var artist: String = "",
    var flavor: String? = null,
    var loyalty: Int = 0,
    var printings: List<String> = listOf(),
    var originalText: String = "",
    var colorsDisplay: List<String> = listOf(),
    var colorsIdentity: List<Color> = mutableListOf(),
    var legalities: MutableList<Legality> = mutableListOf(),
    var faceConvertedManaCost: Int? = null,
    var isArena: Boolean? = null,
    var isMtgo: Boolean? = null,
    var side: Side = Side.A
) {

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

    val isEldrazi: Boolean
        get() = type.contains("Eldrazi")

    fun belongsTo(set: MTGSet) {
        this.set = set
    }

    override fun toString(): String {
        return "MTGCard: [$id,$name,$multiVerseId,$colorsIdentity,$rarity,$quantity]"
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
            Rarity.COMMON -> R.color.common
            Rarity.UNCOMMON -> R.color.uncommon
            Rarity.RARE -> R.color.rare
            Rarity.MYTHIC -> R.color.mythic
        }
    val scryfallImage
        get() = when {
            scryfallId.isNotEmpty() && scryfallSupported -> "https://api.scryfall.com/cards/$scryfallId?format=image${if (side == Side.B) "&face=back" else ""}"
            else -> "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=$multiVerseId&type=card"
        }

    private val scryfallSupported = isNormal || isAdventure || isTransform || isSaga || isHost

    fun getMtgColor(context: Context): Int {
        return ContextCompat.getColor(
            context,
            when {
                colorsIdentity.size > 1 -> R.color.mtg_multi
                colorsIdentity.isEmpty() -> R.color.mtg_other
                colorsIdentity[0] == Color.WHITE -> R.color.mtg_white
                colorsIdentity[0] == Color.BLUE -> R.color.mtg_blue
                colorsIdentity[0] == Color.BLACK -> R.color.mtg_black
                colorsIdentity[0] == Color.RED -> R.color.mtg_red
                colorsIdentity[0] == Color.GREEN -> R.color.mtg_green
                else -> R.color.mtg_other
            }
        )
    }

    val isWhite: Boolean
        get() = manaCost.contains("W") || colorsIdentity.contains(Color.WHITE)

    val isBlue: Boolean
        get() = manaCost.contains("U") || colorsIdentity.contains(Color.BLUE)

    val isBlack: Boolean
        get() = manaCost.contains("B") || colorsIdentity.contains(Color.BLACK)

    val isRed: Boolean
        get() = manaCost.contains("R") || colorsIdentity.contains(Color.RED)

    val isGreen: Boolean
        get() = manaCost.contains("G") || colorsIdentity.contains(Color.GREEN)

    fun hasNoColor(): Boolean {
        return colorsIdentity.isEmpty()
    }

    val isDoubleFaced: Boolean
        get() = layout.equals("double-faced", ignoreCase = true)

    val isTransform: Boolean
        get() = layout.equals("transform", ignoreCase = true)

    private val isSaga: Boolean
        get() = layout.equals("saga", ignoreCase = true)

    private val isNormal: Boolean
        get() = layout.equals("normal", ignoreCase = true)

    private val isAdventure: Boolean
        get() = layout.equals("adventure", ignoreCase = true)

    val isMeld: Boolean
        get() = layout.equals("meld", ignoreCase = true)

    val isHost: Boolean
        get() = layout.equals("host", ignoreCase = true)

    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is MTGCard -> false
            else -> uuid == other.uuid
        }
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + types.hashCode()
        result = 31 * result + subTypes.hashCode()
        result = 31 * result + colorsDisplay.hashCode()
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
        result = 31 * result + number.hashCode()
        result = 31 * result + rulings.hashCode()
        result = 31 * result + names.hashCode()
        result = 31 * result + superTypes.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (flavor?.hashCode() ?: 0)
        result = 31 * result + loyalty
        result = 31 * result + printings.hashCode()
        result = 31 * result + originalText.hashCode()
        result = 31 * result + colorsIdentity.hashCode()
        result = 31 * result + legalities.hashCode()
        result = 31 * result + faceConvertedManaCost.hashCode()
        result = 31 * result + isArena.hashCode()
        result = 31 * result + isMtgo.hashCode()
        result = 31 * result + side.hashCode()
        return result
    }
}

class Legality(val format: String, val legality: String)

enum class Color {
    WHITE, BLUE, BLACK, RED, GREEN
}

enum class Side {
    A, B
}

enum class PriceProvider{
    TCG, MKM
}