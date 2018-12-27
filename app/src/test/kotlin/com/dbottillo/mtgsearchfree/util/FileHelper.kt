package com.dbottillo.mtgsearchfree.util

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.database.mapColor
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.Rarity
import com.dbottillo.mtgsearchfree.model.helper.CreateDBAsyncTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.robolectric.RuntimeEnvironment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringWriter

@Throws(JSONException::class)
fun readSetListJSON(): ArrayList<MTGSet> {
    val jsonString = loadFile("set_list.json")
    val jsonArray = JSONArray(jsonString)
    val sets = ArrayList<MTGSet>()
    for (i in 0 until jsonArray.length()) {
        val setJ = jsonArray.getJSONObject(i)
        try {
            sets.add(MTGSet(id = i, name = setJ.getString("name"), code = setJ.getString("code")))
        } catch (e: Resources.NotFoundException) {
            LOG.e("e: " + e.localizedMessage)
        }
    }
    return sets
}

@Throws(JSONException::class)
fun readSingleSetFile(set: MTGSet): ArrayList<MTGCard> {
    val jsonSetString = loadFile(CreateDBAsyncTask.adjustCode(set.code) + "_x.json")
    val jsonCards = JSONObject(jsonSetString)
    val cardsJ = jsonCards.getJSONArray("cards")
    val cards = ArrayList<MTGCard>()

    for (k in 0 until cardsJ.length()) {
        val cardJ = cardsJ.getJSONObject(k)
        cards.add(cardFromJSON(cardJ, set))
    }

    return cards
}

@Throws(Resources.NotFoundException::class)
private fun loadFile(file: String): String? {
    val inputStream = RuntimeEnvironment.application.classLoader.getResourceAsStream(file)
            ?: throw Resources.NotFoundException("$file not found")

    val writer = StringWriter()
    try {
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val iterator = reader.lineSequence().iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            writer.write(line)
        }
        reader.close()
        inputStream.close()
    } catch (e: IOException) {
        return null
    }

    return writer.toString()
}

@Throws(JSONException::class)
private fun cardFromJSON(jsonObject: JSONObject, set: MTGSet): MTGCard {
    val card = MTGCard()

    card.uuid = jsonObject.getString("uuid")
    card.scryfallId = jsonObject.getString("scryfallId")
    if (jsonObject.has("tcgplayerProductId")) {
        card.tcgplayerProductId = jsonObject.getInt("tcgplayerProductId")
    }
    card.setCardName(jsonObject.getString("name"))
    card.type = jsonObject.getString("type")
    card.belongsTo(set)

    var multicolor = false
    var land: Int
    val artifact: Int = if (jsonObject.getString("type").contains("Artifact")) {
        1
    } else {
        0
    }

    if (jsonObject.has("colors")) {
        val colorsJ = jsonObject.getJSONArray("colors")
        val colors = mutableListOf<String>()
        for (k in 0 until colorsJ.length()) {
            val color = colorsJ.getString(k)
            colors.add(color)
        }
        card.colorsDisplay = colors

        land = 0
    } else {
        land = 1
    }

    if (jsonObject.has("types")) {
        val typesJ = jsonObject.getJSONArray("types")
        for (k in 0 until typesJ.length()) {
            card.addType(typesJ.getString(k))
        }
    }

    if (jsonObject.has("manaCost")) {
        card.manaCost = jsonObject.getString("manaCost")
        land = 0
    }

    card.rarity = when (jsonObject.getString("rarity")) {
        "uncommon", "timeshifted uncommon" -> Rarity.UNCOMMON
        "rare", "timeshifted rare" -> Rarity.RARE
        "mythic", "timeshifted mythic" -> Rarity.MYTHIC
        else -> Rarity.COMMON
    }

    if (jsonObject.has("multiverseid")) {
        card.multiVerseId = jsonObject.getInt("multiverseid")
    }

    var power = ""
    if (jsonObject.has("power")) {
        power = jsonObject.getString("power")
    }
    card.power = power

    var toughness = ""
    if (jsonObject.has("toughness")) {
        toughness = jsonObject.getString("toughness")
    }
    card.toughness = toughness

    if (jsonObject.has("text")) {
        card.text = jsonObject.getString("text")
    }

    var cmc = -1
    if (jsonObject.has("cmc")) {
        cmc = jsonObject.getInt("cmc")
    }
    card.cmc = cmc
    card.isLand = land == 1
    card.isArtifact = artifact == 1

    if (jsonObject.has("rulings")) {
        val rulingsJ = jsonObject.getJSONArray("rulings")
        for (k in 0 until rulingsJ.length()) {
            val ruling = rulingsJ.getJSONObject(k)
            card.addRuling(ruling.getString("text"))
        }
    }

    if (jsonObject.has("layout")) {
        card.layout = jsonObject.getString("layout")
    }

    if (jsonObject.has("number")) {
        card.number = jsonObject.getString("number")
    }

    val gson = Gson()
    val type = object : TypeToken<List<String>>() {
    }.type

    if (jsonObject.has("names")) {
        val names = jsonObject.getString("names")
        if (names != null) {
            val strings = gson.fromJson<List<String>>(names, type)
            card.names = strings
        }
    }
    if (jsonObject.has("supertypes")) {
        val supertypes = jsonObject.getString("supertypes")
        if (supertypes != null) {
            val strings = gson.fromJson<List<String>>(supertypes, type)
            card.superTypes = strings
        }
    }
    if (jsonObject.has("flavor")) {
        card.flavor = jsonObject.getString("flavor")
    }
    if (jsonObject.has("artist")) {
        card.artist = jsonObject.getString("artist")
    }
    if (jsonObject.has("loyalty") && !jsonObject.isNull("loyalty")) {
        card.loyalty = jsonObject.getString("loyalty").toIntOrNull() ?: 0
    }
    if (jsonObject.has("printings")) {
        val printings = jsonObject.getString("printings")
        if (printings != null) {
            val strings = gson.fromJson<List<String>>(printings, type)
            card.printings = strings
        }
    }
    if (jsonObject.has("originalText")) {
        card.originalText = jsonObject.getString("originalText")
    }
    if (jsonObject.has("colorIdentity")) {
        val colorIdentity = jsonObject.getString("colorIdentity")
        val colorsIdentityJ = jsonObject.getJSONArray("colorIdentity")
        if (colorIdentity != null) {
            val strings = gson.fromJson<List<String>>(colorIdentity, type)
            card.colorsIdentity = strings.map { it.mapColor() }
            if (colorsIdentityJ.length() > 1) {
                multicolor = true
            }
        }
    }
    card.isMultiColor = multicolor
    return card
}
