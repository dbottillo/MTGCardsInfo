package com.dbottillo.mtgsearchfree.database

import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.model.Side
import com.dbottillo.mtgsearchfree.util.LOG
import java.util.Arrays
import java.util.Locale

class MTGCardDataSource(
    private val database: SQLiteDatabase,
    private val cardDataSource: CardDataSource
) {

    @Suppress("MagicNumber")
    enum class STANDARD(var setId: Int, var set: String) {
        CORE_21(2, "Core Set 2021"),
        IKORIA(4, "Ikoria: Lair of Behemoths"),
        THEROS_BEYOND_DEATH(10, "Theros Beyond Death"),
        THRONE_OF_ELDRAINE(16, "Throne of Eldraine"),
        CORE_20(19, "Core Set 2020"),
        WAR_OF_THE_SPARK(21, "War of the Spark"),
        RAVNICA_ALLEGIANCE(22, "Ravnica Allegiance"),
        GUILDS_OF_RAVNICA(24, "Guilds of Ravnica");

        companion object {

            val setIds: Array<String>
                get() {
                    return values().map { it.setId.toString() }.toTypedArray()
                }
        }
    }

    fun getSet(set: MTGSet): List<MTGCard> {
        LOG.d("get set  $set")
        val query =
            "SELECT * FROM " + CardDataSource.TABLE + " WHERE " + CardDataSource.COLUMNS.SET_CODE.noun + " = '" + set.code + "';"
        LOG.query(query, set.code!!)

        val cards = ArrayList<MTGCard>()
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val card = cardDataSource.fromCursor(cursor)
                card.belongsTo(set)
                if (card.side == Side.A || card.isMeld) {
                    cards.add(card)
                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        return cards
    }

    @Suppress("SpreadOperator", "ComplexMethod")
    fun searchCards(searchParams: SearchParams): List<MTGCard> {
        LOG.d("search cards  $searchParams")
        val queryComposer = QueryComposer("SELECT * FROM " + CardDataSource.TABLE)
        queryComposer.addLikeParam(
            CardDataSource.COLUMNS.NAME.noun,
            searchParams.name.trim { it <= ' ' }.toLowerCase(Locale.getDefault())
        )
        if (searchParams.types.isNotEmpty()) {
            val types = searchParams.types.split(" ").toTypedArray()
            queryComposer.addMultipleParam(CardDataSource.COLUMNS.TYPE.noun, "LIKE", "AND", *types)
        }
        queryComposer.addLikeParam(
            CardDataSource.COLUMNS.TEXT.noun,
            searchParams.text.trim { it <= ' ' })
        queryComposer.addCMCParam(searchParams.cmc)
        queryComposer.addPTParam(CardDataSource.COLUMNS.POWER.noun, searchParams.power)
        queryComposer.addPTParam(CardDataSource.COLUMNS.TOUGHNESS.noun, searchParams.tough)
        if (searchParams.setId > 0) {
            queryComposer.addParam(CardDataSource.COLUMNS.SET_ID.noun, "==", searchParams.setId)
        }
        if (searchParams.setId == -2) {
            queryComposer.addMultipleParam(
                CardDataSource.COLUMNS.SET_ID.noun,
                "==",
                "OR",
                *STANDARD.setIds
            )
        }
        if (searchParams.atLeastOneColor) {
            when {
                searchParams.exactlyColors -> {
                    val value = searchParams.colors.joinToString(
                        separator = ",",
                        prefix = "[",
                        postfix = "]"
                    ) { "\"$it\"" }
                    queryComposer.addParam(CardDataSource.COLUMNS.COLORS_IDENTITY.noun, "=", value)
                }
                searchParams.includingColors -> {
                    queryComposer.addMultipleParam(
                        CardDataSource.COLUMNS.COLORS_IDENTITY.noun,
                        "LIKE",
                        "AND",
                        *searchParams.colors.toTypedArray()
                    )
                }
                searchParams.atMostColors -> {
                    queryComposer.addMultipleParam(
                        CardDataSource.COLUMNS.COLORS_IDENTITY.noun,
                        "LIKE",
                        "OR",
                        *searchParams.colors.toTypedArray()
                    )
                }
                searchParams.excludingOtherColors -> {
                    queryComposer.addMultipleParam(
                        CardDataSource.COLUMNS.COLORS_IDENTITY.noun,
                        "LIKE",
                        "OR",
                        *searchParams.colors.toTypedArray()
                    )
                    queryComposer.addMultipleParam(
                        CardDataSource.COLUMNS.COLORS_IDENTITY.noun,
                        "NOT LIKE",
                        "AND",
                        *searchParams.notColors.toTypedArray()
                    )
                }
            }
        }
        if (searchParams.atLeastOneRarity) {
            val rarities = ArrayList<String>()
            if (searchParams.isCommon) {
                rarities.add(Rarity.COMMON.value)
            }
            if (searchParams.isUncommon) {
                rarities.add(Rarity.UNCOMMON.value)
            }
            if (searchParams.isRare) {
                rarities.add(Rarity.RARE.value)
            }
            if (searchParams.isMythic) {
                rarities.add(Rarity.MYTHIC.value)
            }
            queryComposer.addMultipleParam(
                CardDataSource.COLUMNS.RARITY.noun,
                "==",
                "OR",
                *rarities.toTypedArray()
            )
        }
        if (searchParams.isLand) {
            queryComposer.addParam(CardDataSource.COLUMNS.LAND.noun, "==", 1)
        }
        if (searchParams.colorless) {
            queryComposer.addIsNullParam(CardDataSource.COLUMNS.COLORS.noun)
        }
        queryComposer.append("ORDER BY " + CardDataSource.COLUMNS.MULTIVERSE_ID.noun + " DESC LIMIT " + LIMIT)

        val output = queryComposer.build()
        val sel = Arrays.copyOf<String, Any>(
            output.selection.toTypedArray(),
            output.selection.size,
            Array<String>::class.java
        )
        LOG.query(output.query, *sel)

        val cursor = database.rawQuery(output.query, sel)

        val cards = ArrayList<MTGCard>()
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val card = cardDataSource.fromCursor(cursor)
                if (card.side == Side.A || card.isMeld) {
                    cards.add(card)
                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        return if (!searchParams.duplicates) {
            cards.distinctBy { it.name }
        } else {
            cards
        }
    }

    fun getRandomCard(number: Int): List<MTGCard> {
        LOG.d("get random card  $number")
        val query = "SELECT * FROM " + CardDataSource.TABLE + " ORDER BY RANDOM() LIMIT " + number
        LOG.query(query)
        val cards = ArrayList<MTGCard>(number)
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                cards.add(cardDataSource.fromCursor(cursor))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return cards
    }

    fun searchCard(name: String, requiredMultiverseId: Boolean = false): MTGCard? {
        LOG.d("search card <$name>")
        val query = if (requiredMultiverseId)
            "SELECT * FROM ${CardDataSource.TABLE} WHERE ${CardDataSource.COLUMNS.NAME.noun}=? AND ${CardDataSource.COLUMNS.MULTIVERSE_ID.noun} IS NOT NULL"
        else
            "SELECT * FROM " + CardDataSource.TABLE + " WHERE " + CardDataSource.COLUMNS.NAME.noun + "=?"
        val selection = arrayOf(name)
        LOG.query(query)
        val cursor = database.rawQuery(query, selection)
        var card: MTGCard? = null
        if (cursor.moveToFirst()) {
            card = cardDataSource.fromCursor(cursor)
        }
        cursor.close()
        return card
    }

    fun searchCard(multiverseid: Int): MTGCard? {
        LOG.d("search card <$multiverseid>")
        val query =
            ("SELECT * FROM " + CardDataSource.TABLE + " WHERE " + CardDataSource.COLUMNS.MULTIVERSE_ID.noun + "=?")
        val selection = arrayOf(multiverseid.toString())
        LOG.query(query)
        val cursor = database.rawQuery(query, selection)
        var card: MTGCard? = null
        if (cursor.moveToFirst()) {
            card = cardDataSource.fromCursor(cursor)
        }
        cursor.close()
        return card
    }

    fun searchCardById(id: Int): MTGCard? {
        LOG.d("search card <$id>")
        val query = "SELECT * FROM " + CardDataSource.TABLE + " WHERE " + "_id=?"
        val selection = arrayOf(id.toString())
        LOG.query(query)
        val cursor = database.rawQuery(query, selection)
        var card: MTGCard? = null
        if (cursor.moveToFirst()) {
            card = cardDataSource.fromCursor(cursor)
        }
        cursor.close()
        return card
    }
}

private const val LIMIT = 400
