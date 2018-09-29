package com.dbottillo.mtgsearchfree.model.helper

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.database.CardDataSource
import com.dbottillo.mtgsearchfree.model.database.CreateDatabaseHelper
import com.dbottillo.mtgsearchfree.model.database.SetDataSource
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.copyDbToSdCard
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.util.*

class CreateDBAsyncTask(inputContext: Context, private val packageName: String) : AsyncTask<String, Void, ArrayList<Any>>() {

    private var error = false
    private var errorMessage: String? = null

    private val context: WeakReference<Context> = WeakReference(inputContext)
    private val mDbHelper: CreateDatabaseHelper = CreateDatabaseHelper(context.get()!!)

    override fun doInBackground(vararg params: String): ArrayList<Any> {
        val result = ArrayList<Any>()

        context.get()?.let {

            val db = mDbHelper.writableDatabase
            db.disableWriteAheadLogging()
            db.delete(SetDataSource.TABLE, null, null)
            db.delete(CardDataSource.TABLE, null, null)

            val setDataSource = SetDataSource(mDbHelper.writableDatabase)
            try {
                val setList = it.resources?.getIdentifier("set_list", "raw", packageName) ?: -1
                val jsonString = loadFile(setList)
                val json = JSONArray(jsonString)
                (json.length() - 1 downTo 0)
                        .map { json.getJSONObject(it) }
                        .forEach { setJ ->
                            loadSet(it, db, setDataSource, setJ)
                        }
            } catch (e: JSONException) {
                LOG.e("error create db async task: " + e.localizedMessage)
                error = true
                errorMessage = e.localizedMessage
            }

            context.get()?.copyDbToSdCard("MTGCardsInfo.db")
        }

        return result
    }

    @Suppress("UNUSED_VARIABLE")
    private fun loadSet(context: Context, db: SQLiteDatabase, setDataSource: SetDataSource, setJ: JSONObject){
        try {
            val setToLoad = setToLoad(context, setJ.getString("code"))
            val jsonSetString = loadFile(setToLoad)

            val newRowId = db.insert(SetDataSource.TABLE, null, setDataSource.fromJSON(setJ))
            LOG.e("row id " + newRowId + " -> " + setJ.getString("code"))

            val jsonCards = JSONObject(jsonSetString)
            val cards = jsonCards.getJSONArray("cards")

            val set = MTGSet(newRowId.toInt(),
                    setJ.getString("code"),
                    setJ.getString("name"))
            //for (int k=0; k<1; k++){

            (0..(cards.length() - 1)).forEach { index ->
                val cardJ = cards.getJSONObject(index)
                //Log.e("MTG", "cardJ $cardJ")

                val newRowId2 = db.insert(CardDataSource.TABLE, null, createContentValueFromJSON(cardJ, set))
                //Log.e("MTG", "row id card $newRowId2")
                //result.add(MTGCard.createCardFromJson(i, cardJ));
            }
        } catch (e: Resources.NotFoundException) {
            LOG.e(setJ.getString("code") + " file not found")
        }
    }

    @Throws(Resources.NotFoundException::class)
    private fun loadFile(file: Int): String {
        val inputStream = context.get()?.resources?.openRawResource(file)

        val writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var n: Int = reader.read(buffer)
            while (n != -1) {
                writer.write(buffer, 0, n)
                n = reader.read(buffer)
            }
            reader.close()
            inputStream?.close()
        } catch (e: IOException) {
            error = true
            errorMessage = e.localizedMessage
        }

        return writer.toString()
    }

    override fun onPostExecute(result: ArrayList<Any>) {
        context.get()?.let {
            if (error) {
                Toast.makeText(it, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(it, "finished", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        fun adjustCode(code: String?): String? {
            val stringToLoad = code?.toLowerCase(Locale.getDefault())
            return when {
                stringToLoad.equals("10e", ignoreCase = true) -> "e10"
                stringToLoad.equals("9ed", ignoreCase = true) -> "ed9"
                stringToLoad.equals("5dn", ignoreCase = true) -> "dn5"
                stringToLoad.equals("8ed", ignoreCase = true) -> "ed8"
                stringToLoad.equals("7ed", ignoreCase = true) -> "ed7"
                stringToLoad.equals("6ed", ignoreCase = true) -> "ed6"
                stringToLoad.equals("5ed", ignoreCase = true) -> "ed5"
                stringToLoad.equals("4ed", ignoreCase = true) -> "ed4"
                stringToLoad.equals("3ed", ignoreCase = true) -> "ed3"
                stringToLoad.equals("2ed", ignoreCase = true) -> "ed2"
                else -> stringToLoad
            }
        }

        fun setToLoad(context: Context, code: String?): Int {
            return context.resources.getIdentifier(adjustCode(code) + "_x", "raw", context.packageName)
        }

        @Throws(JSONException::class)
        private fun createContentValueFromJSON(jsonObject: JSONObject, set: MTGSet): ContentValues {
            val values = ContentValues()

            values.put(CardDataSource.COLUMNS.NAME.noun, jsonObject.getString("name"))
            values.put(CardDataSource.COLUMNS.TYPE.noun, jsonObject.getString("type"))
            values.put(CardDataSource.COLUMNS.SET_ID.noun, set.id)
            values.put(CardDataSource.COLUMNS.SET_NAME.noun, set.name)
            values.put(CardDataSource.COLUMNS.SET_CODE.noun, set.code)

            val multicolor: Int
            var land: Int
            val artifact: Int = if (jsonObject.getString("type").contains("Artifact")) {
                1
            } else {
                0
            }

            if (jsonObject.has("colors")) {
                val colorsJ = jsonObject.getJSONArray("colors")
                val colors = StringBuilder()
                for (k in 0 until colorsJ.length()) {
                    val color = colorsJ.getString(k)
                    colors.append(color)
                    if (k < colorsJ.length() - 1) {
                        colors.append(',')
                    }
                }
                values.put(CardDataSource.COLUMNS.COLORS.noun, colors.toString())

                if (colorsJ.length() > 1) {
                    multicolor = 1
                } else {
                    multicolor = 0
                }
                land = 0
            } else {
                multicolor = 0
                land = 1
            }

            if (jsonObject.has("types")) {
                val typesJ = jsonObject.getJSONArray("types")
                val types = StringBuilder()
                for (k in 0 until typesJ.length()) {
                    types.append(typesJ.getString(k))
                    if (k < typesJ.length() - 1) {
                        types.append(',')
                    }
                }
                values.put(CardDataSource.COLUMNS.TYPES.noun, types.toString())
            }

            if (jsonObject.has("manaCost")) {
                values.put(CardDataSource.COLUMNS.MANA_COST.noun, jsonObject.getString("manaCost"))
                land = 0
            }
            values.put(CardDataSource.COLUMNS.RARITY.noun, jsonObject.getString("rarity"))

            if (jsonObject.has("multiverseid")) {
                values.put(CardDataSource.COLUMNS.MULTIVERSE_ID.noun, jsonObject.getInt("multiverseid"))
            }

            var power = ""
            if (jsonObject.has("power")) {
                power = jsonObject.getString("power")
            }
            values.put(CardDataSource.COLUMNS.POWER.noun, power)

            var toughness = ""
            if (jsonObject.has("toughness")) {
                toughness = jsonObject.getString("toughness")
            }
            values.put(CardDataSource.COLUMNS.TOUGHNESS.noun, toughness)

            if (jsonObject.has("text")) {
                values.put(CardDataSource.COLUMNS.TEXT.noun, jsonObject.getString("text"))
            }

            var cmc = -1
            if (jsonObject.has("cmc")) {
                cmc = jsonObject.getInt("cmc")
            }
            values.put(CardDataSource.COLUMNS.CMC.noun, cmc)
            values.put(CardDataSource.COLUMNS.MULTICOLOR.noun, multicolor)
            values.put(CardDataSource.COLUMNS.LAND.noun, land)
            values.put(CardDataSource.COLUMNS.ARTIFACT.noun, artifact)

            if (jsonObject.has("rulings")) {
                val rulingsJ = jsonObject.getJSONArray("rulings")
                values.put(CardDataSource.COLUMNS.RULINGS.noun, rulingsJ.toString())
            }

            if (jsonObject.has("layout")) {
                values.put(CardDataSource.COLUMNS.LAYOUT.noun, jsonObject.getString("layout"))
            }

            if (jsonObject.has("number")) {
                values.put(CardDataSource.COLUMNS.NUMBER.noun, jsonObject.getString("number"))
            }

            if (jsonObject.has("names")) {
                values.put(CardDataSource.COLUMNS.NAMES.noun, jsonObject.getString("names"))
            }
            if (jsonObject.has("supertypes")) {
                values.put(CardDataSource.COLUMNS.SUPER_TYPES.noun, jsonObject.getString("supertypes"))
            }
            if (jsonObject.has("flavor")) {
                values.put(CardDataSource.COLUMNS.FLAVOR.noun, jsonObject.getString("flavor"))
            }
            if (jsonObject.has("artist")) {
                values.put(CardDataSource.COLUMNS.ARTIST.noun, jsonObject.getString("artist"))
            }
            if (jsonObject.has("loyalty") && !jsonObject.isNull("loyalty")) {
                values.put(CardDataSource.COLUMNS.LOYALTY.noun, jsonObject.getInt("loyalty"))
            }
            if (jsonObject.has("printings")) {
                values.put(CardDataSource.COLUMNS.PRINTINGS.noun, jsonObject.getString("printings"))
            }
            if (jsonObject.has("legalities")) {
                values.put(CardDataSource.COLUMNS.LEGALITIES.noun, jsonObject.getString("legalities"))
            }
            if (jsonObject.has("originalText")) {
                values.put(CardDataSource.COLUMNS.ORIGINAL_TEXT.noun, jsonObject.getString("originalText"))
            }
            if (jsonObject.has("mciNumber")) {
                values.put(CardDataSource.COLUMNS.MCI_NUMBER.noun, jsonObject.getString("mciNumber"))
            }
            if (jsonObject.has("colorIdentity")) {
                values.put(CardDataSource.COLUMNS.COLORS_IDENTITY.noun, jsonObject.getString("colorIdentity"))
            }

            return values
        }
    }

}
