package com.dbottillo.mtgsearchfree.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.VisibleForTesting
import com.crashlytics.android.Crashlytics
import com.dbottillo.mtgsearchfree.model.Color
import com.dbottillo.mtgsearchfree.model.Legality
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.util.LOG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class CardDataSource(private val database: SQLiteDatabase, private val gson: Gson) {

    val cards: List<MTGCard>
        get() {
            val cursor = database.rawQuery("select * from $TABLE", null)
            val cards = ArrayList<MTGCard>()
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    cards.add(fromCursor(cursor))
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return cards
        }

    enum class COLUMNS(val noun: String, val type: String) {
        NAME("name", "TEXT"),
        TYPE("type", "TEXT"),
        TYPES("types", "TEXT"),
        SUB_TYPES("subtypes", "TEXT"),
        COLORS("colors", "TEXT"),
        CMC("cmc", "INTEGER"),
        RARITY("rarity", "TEXT"),
        POWER("power", "TEXT"),
        TOUGHNESS("toughness", "TEXT"),
        MANA_COST("manaCost", "TEXT"),
        TEXT("text", "TEXT"),
        MULTICOLOR("multicolor", "INTEGER"),
        LAND("land", "INTEGER"),
        ARTIFACT("artifact", "INTEGER"),
        MULTIVERSE_ID("multiVerseId", "INTEGER"),
        SET_ID("setId", "INTEGER"),
        SET_NAME("setName", "TEXT"),
        RULINGS("rulings", "TEXT"),
        LAYOUT("layout", "TEXT"),
        SET_CODE("setCode", "TEXT"),
        NUMBER("number", "TEXT"),
        NAMES("names", "TEXT"),
        SUPER_TYPES("supertypes", "TEXT"),
        FLAVOR("flavor", "TEXT"),
        ARTIST("artist", "TEXT"),
        LOYALTY("loyalty", "INTEGER"),
        PRINTINGS("printings", "TEXT"),
        LEGALITIES("legalities", "TEXT"),
        ORIGINAL_TEXT("originalText", "TEXT"),
        COLORS_IDENTITY("colorIdentity", "TEXT"),
        UUID("uuid", "TEXT"),
        SCRYFALLID("scryfallId", "TEXT"),
        TCG_PLAYER_PRODUCT_ID("tcgplayerProductId", "INTEGER"),
        TCG_PLAYER_PURCHASE_URL("tcgplayerPurchaseUrl", "TEXT")
    }

    fun saveCard(card: MTGCard): Long {
        return database.insertWithOnConflict(TABLE, null, createContentValue(card), SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun removeCard(card: MTGCard) {
        val removeQuery = "DELETE FROM $TABLE where _id=?"
        val cursor = database.rawQuery(removeQuery, arrayOf(card.id.toString()))
        cursor.moveToFirst()
        cursor.close()
    }

    fun createContentValue(card: MTGCard): ContentValues {
        val values = ContentValues()
        values.put(COLUMNS.NAME.noun, card.name)
        values.put(COLUMNS.TYPE.noun, card.type)
        values.put(COLUMNS.SET_ID.noun, card.set!!.id)
        values.put(COLUMNS.SET_NAME.noun, card.set!!.name)
        values.put(COLUMNS.SET_CODE.noun, card.set!!.code)
        values.put(COLUMNS.COLORS.noun, card.colorsDisplay.joinToString(","))
        val types = card.types
        if (types.size > 0) {
            val typ = StringBuilder()
            for (k in types.indices) {
                typ.append(types[k])
                if (k < types.size - 1) {
                    typ.append(',')
                }
            }
            values.put(COLUMNS.TYPES.noun, typ.toString())
        }
        val subTypes = card.subTypes
        if (subTypes.size > 0) {
            val typ = StringBuilder()
            for (k in subTypes.indices) {
                typ.append(subTypes[k])
                if (k < subTypes.size - 1) {
                    typ.append(',')
                }
            }
            values.put(COLUMNS.SUB_TYPES.noun, typ.toString())
        }
        values.put(COLUMNS.MANA_COST.noun, card.manaCost)
        values.put(COLUMNS.RARITY.noun, card.rarity.value)
        values.put(COLUMNS.MULTIVERSE_ID.noun, card.multiVerseId)
        values.put(COLUMNS.POWER.noun, card.power)
        values.put(COLUMNS.TOUGHNESS.noun, card.toughness)
        values.put(COLUMNS.TEXT.noun, card.text)
        values.put(COLUMNS.CMC.noun, card.cmc)
        values.put(COLUMNS.MULTICOLOR.noun, card.isMultiColor)
        values.put(COLUMNS.LAND.noun, card.isLand)
        values.put(COLUMNS.ARTIFACT.noun, card.isArtifact)
        val rulings = card.rulings
        if (rulings.size > 0) {
            val rules = JSONArray()
            for (rule in rulings) {
                val rulJ = JSONObject()
                try {
                    rulJ.put("text", rule)
                    rules.put(rulJ)
                } catch (e: JSONException) {
                    Crashlytics.logException(e)
                    LOG.e(e)
                }
            }
            values.put(COLUMNS.RULINGS.noun, rules.toString())
        }
        values.put(COLUMNS.LAYOUT.noun, card.layout)
        values.put(COLUMNS.NUMBER.noun, card.number)

        values.put(COLUMNS.NAMES.noun, gson.toJson(card.names))
        values.put(COLUMNS.SUPER_TYPES.noun, gson.toJson(card.superTypes))
        values.put(COLUMNS.FLAVOR.noun, card.flavor)
        values.put(COLUMNS.ARTIST.noun, card.artist)
        values.put(COLUMNS.LOYALTY.noun, card.loyalty)
        values.put(COLUMNS.PRINTINGS.noun, gson.toJson(card.printings))
        values.put(COLUMNS.ORIGINAL_TEXT.noun, card.originalText)
        values.put(COLUMNS.COLORS_IDENTITY.noun, gson.toJson(card.colorsIdentity.map { it.unmap() }))
        val legalities = card.legalities
        if (legalities.size > 0) {
            val legalitiesJ = JSONArray()
            for (legality in legalities) {
                val legJ = JSONObject()
                try {
                    legJ.put("format", legality.format)
                    legJ.put("legality", legality.legality)
                    legalitiesJ.put(legJ)
                } catch (e: JSONException) {
                    Crashlytics.logException(e)
                    LOG.e(e)
                }
            }
            values.put(COLUMNS.LEGALITIES.noun, legalitiesJ.toString())
        }
        values.put(COLUMNS.UUID.noun, card.uuid)
        values.put(COLUMNS.SCRYFALLID.noun, card.scryfallId)
        values.put(COLUMNS.TCG_PLAYER_PRODUCT_ID.noun, card.tcgplayerProductId)
        values.put(COLUMNS.TCG_PLAYER_PURCHASE_URL.noun, card.tcgplayerPurchaseUrl)
        return values
    }

    fun fromCursor(cursor: Cursor, fullCard: Boolean = true): MTGCard {
        /*if (cursor.getColumnIndex("_id") == -1) {
            return null
        }*/
        val card = MTGCard(cursor.getInt(cursor.getColumnIndex("_id")))
        if (cursor.getColumnIndex(COLUMNS.MULTIVERSE_ID.noun) != -1) {
            card.multiVerseId = cursor.getInt(cursor.getColumnIndex(COLUMNS.MULTIVERSE_ID.noun))
        }
        if (!fullCard) {
            return card
        }
        card.type = cursor.getString(cursor.getColumnIndex(COLUMNS.TYPE.noun))
        card.setCardName(cursor.getString(cursor.getColumnIndex(COLUMNS.NAME.noun)))

        val setId = cursor.getInt(cursor.getColumnIndex(COLUMNS.SET_ID.noun))
        val set = MTGSet(setId, null, cursor.getString(cursor.getColumnIndex(COLUMNS.SET_NAME.noun)))
        if (cursor.getColumnIndex(COLUMNS.SET_CODE.noun) > -1) {
            set.code = cursor.getString(cursor.getColumnIndex(COLUMNS.SET_CODE.noun))
        }
        card.belongsTo(set)

        if (cursor.getColumnIndex(COLUMNS.COLORS.noun) != -1) {
            val colors = cursor.getString(cursor.getColumnIndex(COLUMNS.COLORS.noun))
            card.colorsDisplay = if (colors.isNullOrEmpty()) listOf() else colors.split(",")
        }
        if (cursor.getColumnIndex(COLUMNS.TYPES.noun) != -1) {
            val types = cursor.getString(cursor.getColumnIndex(COLUMNS.TYPES.noun))
            types?.split(",")?.forEach {
                card.addType(it)
            }
        }
        if (cursor.getColumnIndex(COLUMNS.SUB_TYPES.noun) != -1) {
            val subTypes = cursor.getString(cursor.getColumnIndex(COLUMNS.SUB_TYPES.noun))
            subTypes?.split(",")?.forEach {
                card.addSubType(it)
            }
        }

        if (cursor.getColumnIndex(COLUMNS.MANA_COST.noun) != -1) {
            val manaCost = cursor.getString(cursor.getColumnIndex(COLUMNS.MANA_COST.noun))
            if (manaCost != null) {
                card.manaCost = manaCost
            }
        }

        val rarity: Rarity = when (cursor.getString(cursor.getColumnIndex(COLUMNS.RARITY.noun))) {
            "uncommon", "timeshifted uncommon" -> Rarity.UNCOMMON
            "rare", "timeshifted rare" -> Rarity.RARE
            "mythic", "timeshifted mythic" -> Rarity.MYTHIC
            else -> Rarity.COMMON
        }
        card.rarity = rarity
        card.power = cursor.getString(cursor.getColumnIndex(COLUMNS.POWER.noun))
        card.toughness = cursor.getString(cursor.getColumnIndex(COLUMNS.TOUGHNESS.noun))

        if (cursor.getColumnIndex(COLUMNS.TEXT.noun) != -1) {
            val text = cursor.getString(cursor.getColumnIndex(COLUMNS.TEXT.noun))
            if (text != null) {
                card.text = text
            }
        }

        card.cmc = cursor.getInt(cursor.getColumnIndex(COLUMNS.CMC.noun))
        card.isMultiColor = cursor.getInt(cursor.getColumnIndex(COLUMNS.MULTICOLOR.noun)) == 1
        card.isLand = cursor.getInt(cursor.getColumnIndex(COLUMNS.LAND.noun)) == 1
        card.isArtifact = cursor.getInt(cursor.getColumnIndex(COLUMNS.ARTIFACT.noun)) == 1

        val rulings = cursor.getString(cursor.getColumnIndex(COLUMNS.RULINGS.noun))
        if (rulings != null) {
            try {
                val jsonArray = JSONArray(rulings)
                for (i in 0 until jsonArray.length()) {
                    val rule = jsonArray.getJSONObject(i)
                    card.addRuling(rule.getString("text"))
                }
            } catch (e: JSONException) {
                Crashlytics.logException(e)
                LOG.e(e)
            }
        }

        val type = object : TypeToken<List<String>>() {}.type

        if (cursor.getColumnIndex(COLUMNS.LAYOUT.noun) != -1) {
            card.layout = cursor.getString(cursor.getColumnIndex(COLUMNS.LAYOUT.noun))
        }
        if (cursor.getColumnIndex(COLUMNS.NUMBER.noun) != -1) {
            card.number = cursor.getString(cursor.getColumnIndex(COLUMNS.NUMBER.noun)) ?: ""
        }

        if (cursor.getColumnIndex(COLUMNS.NAMES.noun) != -1) {
            val names = cursor.getString(cursor.getColumnIndex(COLUMNS.NAMES.noun))
            if (names != null) {
                card.names = gson.fromJson<List<String>>(names, type)
            }
        }
        if (cursor.getColumnIndex(COLUMNS.SUPER_TYPES.noun) != -1) {
            val superTypes = cursor.getString(cursor.getColumnIndex(COLUMNS.SUPER_TYPES.noun))
            if (superTypes != null) {
                card.superTypes = gson.fromJson<List<String>>(superTypes, type)
            }
        }
        if (cursor.getColumnIndex(COLUMNS.LOYALTY.noun) != -1) {
            card.loyalty = cursor.getInt(cursor.getColumnIndex(COLUMNS.LOYALTY.noun))
        }
        val artist = getString(cursor, COLUMNS.ARTIST)
        if (artist != null) {
            card.artist = artist
        }
        val flavor = getString(cursor, COLUMNS.FLAVOR)
        if (flavor != null) {
            card.flavor = flavor
        }
        if (cursor.getColumnIndex(COLUMNS.PRINTINGS.noun) != -1) {
            val printings = cursor.getString(cursor.getColumnIndex(COLUMNS.PRINTINGS.noun))
            if (printings != null) {
                card.printings = gson.fromJson<List<String>>(printings, type)
            }
        }
        if (cursor.getColumnIndex(COLUMNS.ORIGINAL_TEXT.noun) != -1) {
            val originalText = cursor.getString(cursor.getColumnIndex(COLUMNS.ORIGINAL_TEXT.noun))
            if (originalText != null) {
                card.originalText = originalText
            }
        }

        if (cursor.getColumnIndex(COLUMNS.COLORS_IDENTITY.noun) != -1) {
            val colorsIdentity = cursor.getString(cursor.getColumnIndex(COLUMNS.COLORS_IDENTITY.noun))
            if (colorsIdentity != null) {
                val colors = gson.fromJson<List<String>>(colorsIdentity, type)
                if (colors?.isNotEmpty() == true) {
                    card.colorsIdentity = colors.map { it.mapColor() }
                }
            }
        }

        val legalities = cursor.getString(cursor.getColumnIndex(COLUMNS.LEGALITIES.noun))
        if (legalities != null) {
            try {
                val jsonArray = JSONArray(legalities)
                for (i in 0 until jsonArray.length()) {
                    val rule = jsonArray.getJSONObject(i)
                    val format = rule.getString("format")
                    val legality = rule.getString("legality")
                    card.addLegality(Legality(format, legality))
                }
            } catch (e: JSONException) {
                try {
                    val legalitiesJ = JSONObject(legalities)
                    val keys = legalitiesJ.keys()
                    while (keys.hasNext()) {
                        val format = keys.next() as String
                        val legality = legalitiesJ.getString(format)
                        card.addLegality(Legality(format, legality))
                    }
                } catch (e2: JSONException) {
                    Crashlytics.logException(e2)
                    LOG.e(e2)
                }
            }
        }

        if (cursor.getColumnIndex(COLUMNS.UUID.noun) != -1) {
            card.uuid = cursor.getString(cursor.getColumnIndex(COLUMNS.UUID.noun)) ?: ""
        }

        if (cursor.getColumnIndex(COLUMNS.SCRYFALLID.noun) != -1) {
            card.scryfallId = cursor.getString(cursor.getColumnIndex(COLUMNS.SCRYFALLID.noun)) ?: ""
        }

        if (cursor.getColumnIndex(COLUMNS.TCG_PLAYER_PRODUCT_ID.noun) != -1) {
            card.tcgplayerProductId = cursor.getInt(cursor.getColumnIndex(COLUMNS.TCG_PLAYER_PRODUCT_ID.noun))
        }

        if (cursor.getColumnIndex(COLUMNS.TCG_PLAYER_PURCHASE_URL.noun) != -1) {
            card.tcgplayerPurchaseUrl = cursor.getString(cursor.getColumnIndex(COLUMNS.TCG_PLAYER_PURCHASE_URL.noun)) ?: ""
        }

        return card
    }

    private fun getString(cursor: Cursor, column: COLUMNS): String? {
        return if (cursor.getColumnIndex(column.noun) != -1) {
            cursor.getString(cursor.getColumnIndex(column.noun))
        } else null
    }

    companion object {

        const val LIMIT = 400
        const val TABLE = "MTGCard"

        internal val SQL_ADD_COLUMN_RULINGS = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.RULINGS.noun + " " + COLUMNS.RULINGS.type)

        internal val SQL_ADD_COLUMN_LAYOUT = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.LAYOUT.noun + " " + COLUMNS.LAYOUT.type)

        internal val SQL_ADD_COLUMN_SET_CODE = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.SET_CODE.noun + " " + COLUMNS.SET_CODE.type)

        internal val SQL_ADD_COLUMN_NUMBER = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.NUMBER.noun + " " + COLUMNS.NUMBER.type)

        internal val SQL_ADD_COLUMN_NAMES = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.NAMES.noun + " " + COLUMNS.NAMES.type)

        internal val SQL_ADD_COLUMN_SUPER_TYPES = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.SUPER_TYPES.noun + " " + COLUMNS.SUPER_TYPES.type)

        internal val SQL_ADD_COLUMN_FLAVOR = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.FLAVOR.noun + " " + COLUMNS.FLAVOR.type)

        internal val SQL_ADD_COLUMN_ARTIST = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.ARTIST.noun + " " + COLUMNS.ARTIST.type)

        internal val SQL_ADD_COLUMN_LOYALTY = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.LOYALTY.noun + " " + COLUMNS.LOYALTY.type)

        internal val SQL_ADD_COLUMN_PRINTINGS = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.PRINTINGS.noun + " " + COLUMNS.PRINTINGS.type)

        internal val SQL_ADD_COLUMN_LEGALITIES = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.LEGALITIES.noun + " " + COLUMNS.LEGALITIES.type)

        internal val SQL_ADD_COLUMN_ORIGINAL_TEXT = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.ORIGINAL_TEXT.noun + " " + COLUMNS.ORIGINAL_TEXT.type)

        internal val SQL_ADD_COLUMN_COLORS_IDENTITY = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.COLORS_IDENTITY.noun + " " + COLUMNS.COLORS_IDENTITY.type)

        internal val SQL_ADD_COLUMN_UUID = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.UUID.noun + " " + COLUMNS.UUID.type)

        internal val SQL_ADD_COLUMN_SCRYFALLID = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.SCRYFALLID.noun + " " + COLUMNS.SCRYFALLID.type)

        internal val SQL_ADD_COLUMN_TCG_PLAYER_PRODUCT_ID = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.TCG_PLAYER_PRODUCT_ID.noun + " " + COLUMNS.TCG_PLAYER_PRODUCT_ID.type)

        internal val SQL_ADD_COLUMN_TCG_PLAYER_PRODUCT_URL = ("ALTER TABLE " +
                TABLE + " ADD COLUMN " +
                COLUMNS.TCG_PLAYER_PURCHASE_URL.noun + " " + COLUMNS.TCG_PLAYER_PURCHASE_URL.type)

        fun generateCreateTable(): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ")
            for (column in COLUMNS.values()) {
                builder.append(column.noun).append(' ').append(column.type)
                if (column != COLUMNS.TCG_PLAYER_PURCHASE_URL) {
                    builder.append(',')
                }
            }
            return builder.append(')').toString()
        }

        private fun getLastColumn(version: Int): COLUMNS {
            return when {
                version < TWO -> COLUMNS.SET_NAME
                version < THREE -> COLUMNS.LAYOUT
                version < SEVEN -> COLUMNS.NUMBER
                version < EIGTH -> COLUMNS.ORIGINAL_TEXT
                version < NINE -> COLUMNS.COLORS_IDENTITY
                version < TEN -> COLUMNS.UUID
                version < ELEVEN -> COLUMNS.TCG_PLAYER_PRODUCT_ID
                else -> COLUMNS.TCG_PLAYER_PURCHASE_URL
            }
        }

        @VisibleForTesting
        fun generateCreateTable(version: Int): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ")
            val lastColumn = getLastColumn(version)
            for (column in COLUMNS.values()) {
                if (shouldAddColumn(column, version)) {
                    builder.append(column.noun).append(' ').append(column.type)
                    if (column != lastColumn) {
                        builder.append(',')
                    }
                }
            }
            return builder.append(')').toString()
        }

        private fun shouldAddColumn(column: COLUMNS, version: Int): Boolean {
            var addColumn = true
            if ((column == COLUMNS.RULINGS || column == COLUMNS.LAYOUT) && version <= 1) {
                addColumn = false
            } else if ((column == COLUMNS.NUMBER || column == COLUMNS.SET_CODE) && version <= 2) {
                addColumn = false
            } else if ((column == COLUMNS.NAMES || column == COLUMNS.SUPER_TYPES ||
                            column == COLUMNS.FLAVOR || column == COLUMNS.ARTIST ||
                            column == COLUMNS.LOYALTY || column == COLUMNS.PRINTINGS ||
                            column == COLUMNS.LEGALITIES || column == COLUMNS.ORIGINAL_TEXT) && version <= 6) {
                addColumn = false
            } else if (column == COLUMNS.COLORS_IDENTITY && version <= SEVEN) {
                addColumn = false
            } else if (column == COLUMNS.UUID && version <= EIGTH) {
                addColumn = false
            } else if ((column == COLUMNS.SCRYFALLID || column == COLUMNS.TCG_PLAYER_PRODUCT_ID) &&
                    version <= NINE) {
                addColumn = false
            } else if ((column == COLUMNS.TCG_PLAYER_PURCHASE_URL) && version <= TEN) {
                addColumn = false
            }
            return addColumn
        }
    }
}

fun String.mapColor(): Color {
    return when (this) {
        "W" -> Color.WHITE
        "U" -> Color.BLUE
        "B" -> Color.BLACK
        "R" -> Color.RED
        "G" -> Color.GREEN
        else -> throw UnsupportedOperationException("color not supported")
    }
}

fun Color.unmap(): String {
    return when (this) {
        Color.WHITE -> "W"
        Color.BLUE -> "U"
        Color.BLACK -> "B"
        Color.RED -> "R"
        Color.GREEN -> "G"
    }
}

private const val TWO = 2
private const val THREE = 3
private const val SEVEN = 7
private const val EIGTH = 8
private const val NINE = 9
private const val TEN = 10
private const val ELEVEN = 11