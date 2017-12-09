package com.dbottillo.mtgsearchfree.model.helper

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.os.AsyncTask
import android.widget.Toast
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.database.CardDataSource
import com.dbottillo.mtgsearchfree.model.database.CreateDatabaseHelper
import com.dbottillo.mtgsearchfree.model.database.SetDataSource
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.LOG
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
    private val mDbHelper: CreateDatabaseHelper = CreateDatabaseHelper(context.get())

    override fun doInBackground(vararg params: String): ArrayList<Any> {
        val result = ArrayList<Any>()

        context.get()?.let {

            val db = mDbHelper.writableDatabase
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
                            try {
                                val setToLoad = setToLoad(it, setJ.getString("code"))
                                val jsonSetString = loadFile(setToLoad)

                                val newRowId = db.insert(SetDataSource.TABLE, null, setDataSource.fromJSON(setJ))
                                LOG.e("row id " + newRowId + " -> " + setJ.getString("code"))

                                val jsonCards = JSONObject(jsonSetString)
                                val cards = jsonCards.getJSONArray("cards")

                                val set = MTGSet(newRowId.toInt())
                                set.name = setJ.getString("name")
                                set.code = setJ.getString("code")
                                //for (int k=0; k<1; k++){

                                (0..(cards.length() - 1)).forEach { index ->
                                    val cardJ = cards.getJSONObject(index)
                                    //Log.e("BBM", "cardJ "+cardJ)

                                    val newRowId2 = db.insert(CardDataSource.TABLE, null, createContentValueFromJSON(cardJ, set))
                                    //Log.e("MTG", "row id card"+newRowId2)
                                    //result.add(MTGCard.createCardFromJson(i, cardJ));
                                }
                            } catch (e: Resources.NotFoundException) {
                                LOG.e(setJ.getString("code") + " file not found")
                            }

                            /*
                        Danieles-MacBook-Pro:~ danielebottillo$ adb pull /sdcard/MTGCardsInfo.db
                        */
                        }
            } catch (e: JSONException) {
                LOG.e("error create db async task: " + e.localizedMessage)
                error = true
                errorMessage = e.localizedMessage
            }

            FileUtil.copyDbToSdCard(context.get(), "MTGCardsInfo.db")
        }

        return result
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

        fun setToLoad(context: Context, code: String): Int {
            var stringToLoad = code.toLowerCase(Locale.getDefault())
            when {
                stringToLoad.equals("10e", ignoreCase = true) -> stringToLoad = "e10"
                stringToLoad.equals("9ed", ignoreCase = true) -> stringToLoad = "ed9"
                stringToLoad.equals("5dn", ignoreCase = true) -> stringToLoad = "dn5"
                stringToLoad.equals("8ed", ignoreCase = true) -> stringToLoad = "ed8"
                stringToLoad.equals("7ed", ignoreCase = true) -> stringToLoad = "ed7"
                stringToLoad.equals("6ed", ignoreCase = true) -> stringToLoad = "ed6"
                stringToLoad.equals("5ed", ignoreCase = true) -> stringToLoad = "ed5"
                stringToLoad.equals("4ed", ignoreCase = true) -> stringToLoad = "ed4"
                stringToLoad.equals("3ed", ignoreCase = true) -> stringToLoad = "ed3"
                stringToLoad.equals("2ed", ignoreCase = true) -> stringToLoad = "ed2"
            }
            return context.resources.getIdentifier(stringToLoad + "_x", "raw", context.packageName)
        }

        @Throws(JSONException::class)
        private fun createContentValueFromJSON(jsonObject: JSONObject, set: MTGSet): ContentValues {
            val values = ContentValues()

            values.put(CardDataSource.COLUMNS.NAME.getName(), jsonObject.getString("name"))
            values.put(CardDataSource.COLUMNS.TYPE.getName(), jsonObject.getString("type"))
            values.put(CardDataSource.COLUMNS.SET_ID.getName(), set.id)
            values.put(CardDataSource.COLUMNS.SET_NAME.getName(), set.name)
            values.put(CardDataSource.COLUMNS.SET_CODE.getName(), set.code)

            val multicolor: Int
            var land: Int
            val artifact: Int

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
                values.put(CardDataSource.COLUMNS.COLORS.getName(), colors.toString())

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
                values.put(CardDataSource.COLUMNS.TYPES.getName(), types.toString())
            }

            artifact = if (jsonObject.getString("type").contains("Artifact")) {
                1
            } else {
                0
            }

            if (jsonObject.has("manaCost")) {
                values.put(CardDataSource.COLUMNS.MANA_COST.getName(), jsonObject.getString("manaCost"))
                land = 0
            }
            values.put(CardDataSource.COLUMNS.RARITY.getName(), jsonObject.getString("rarity"))

            if (jsonObject.has("multiverseid")) {
                values.put(CardDataSource.COLUMNS.MULTIVERSE_ID.getName(), jsonObject.getInt("multiverseid"))
            }

            var power = ""
            if (jsonObject.has("power")) {
                power = jsonObject.getString("power")
            }
            values.put(CardDataSource.COLUMNS.POWER.getName(), power)

            var toughness = ""
            if (jsonObject.has("toughness")) {
                toughness = jsonObject.getString("toughness")
            }
            values.put(CardDataSource.COLUMNS.TOUGHNESS.getName(), toughness)

            if (jsonObject.has("text")) {
                values.put(CardDataSource.COLUMNS.TEXT.getName(), jsonObject.getString("text"))
            }

            var cmc = -1
            if (jsonObject.has("cmc")) {
                cmc = jsonObject.getInt("cmc")
            }
            values.put(CardDataSource.COLUMNS.CMC.getName(), cmc)
            values.put(CardDataSource.COLUMNS.MULTICOLOR.getName(), multicolor)
            values.put(CardDataSource.COLUMNS.LAND.getName(), land)
            values.put(CardDataSource.COLUMNS.ARTIFACT.getName(), artifact)

            if (jsonObject.has("rulings")) {
                val rulingsJ = jsonObject.getJSONArray("rulings")
                values.put(CardDataSource.COLUMNS.RULINGS.getName(), rulingsJ.toString())
            }

            if (jsonObject.has("layout")) {
                values.put(CardDataSource.COLUMNS.LAYOUT.getName(), jsonObject.getString("layout"))
            }

            if (jsonObject.has("number")) {
                values.put(CardDataSource.COLUMNS.NUMBER.getName(), jsonObject.getString("number"))
            }

            if (jsonObject.has("names")) {
                values.put(CardDataSource.COLUMNS.NAMES.getName(), jsonObject.getString("names"))
            }
            if (jsonObject.has("supertypes")) {
                values.put(CardDataSource.COLUMNS.SUPER_TYPES.getName(), jsonObject.getString("supertypes"))
            }
            if (jsonObject.has("flavor")) {
                values.put(CardDataSource.COLUMNS.FLAVOR.getName(), jsonObject.getString("flavor"))
            }
            if (jsonObject.has("artist")) {
                values.put(CardDataSource.COLUMNS.ARTIST.getName(), jsonObject.getString("artist"))
            }
            if (jsonObject.has("loyalty") && !jsonObject.isNull("loyalty")) {
                values.put(CardDataSource.COLUMNS.LOYALTY.getName(), jsonObject.getInt("loyalty"))
            }
            if (jsonObject.has("printings")) {
                values.put(CardDataSource.COLUMNS.PRINTINGS.getName(), jsonObject.getString("printings"))
            }
            if (jsonObject.has("legalities")) {
                values.put(CardDataSource.COLUMNS.LEGALITIES.getName(), jsonObject.getString("legalities"))
            }
            if (jsonObject.has("originalText")) {
                values.put(CardDataSource.COLUMNS.ORIGINAL_TEXT.getName(), jsonObject.getString("originalText"))
            }
            if (jsonObject.has("mciNumber")) {
                values.put(CardDataSource.COLUMNS.MCI_NUMBER.getName(), jsonObject.getString("mciNumber"))
            }
            if (jsonObject.has("colorIdentity")) {
                values.put(CardDataSource.COLUMNS.COLORS_IDENTITY.getName(), jsonObject.getString("colorIdentity"))
            }

            return values
        }
    }

}
