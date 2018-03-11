package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextUtils

import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Arrays

class FileUtil(private val fileManager: FileManagerI) {

    @Throws(Exception::class)
    fun readFileContent(uri: Uri): CardsBucket {
        val `is` = fileManager.loadUri(uri)
        val bucket: CardsBucket
        try {
            bucket = readFileStream(`is`)
            if (bucket.key == null) {
                bucket.key = uri.lastPathSegment
            }
        } catch (e: Exception) {
            `is`.close()
            throw e
        }

        return bucket
    }

    @Throws(Exception::class)
    private fun readFileStream(input: InputStream): CardsBucket {
        val cards = ArrayList<MTGCard>()
        val bucket = CardsBucket()
        val br = BufferedReader(InputStreamReader(input, "UTF-8"))

        var side = false
        var numberOfEmptyLines = 0
        var line = br.readLine()
        while (line != null) {
            if (line.startsWith("//")) {
                // title
                if (bucket.key == null) {
                    bucket.key = line.replace("//", "")
                }
            } else if (line.isEmpty()) {
                numberOfEmptyLines++
                // from here on all the cards belong to side
                if (numberOfEmptyLines >= 2) {
                    side = true
                }
            } else {
                val card = generateCard(line.replace("SB: ", ""))
                if (line.startsWith("SB: ")) {
                    card.isSideboard = true
                } else {
                    card.isSideboard = side
                }
                cards.add(card)
            }
            line = br.readLine()
        }
        br.close()
        bucket.cards = cards
        return bucket
    }

    private fun generateCard(line: String): MTGCard {
        val items = ArrayList(Arrays.asList(*line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
        val first = items.removeAt(0)
        val rest = TextUtils.join(" ", items)
        val card = MTGCard()
        card.quantity = Integer.parseInt(first)
        card.setCardName(rest)
        return card
    }

    fun downloadDeckToSdCard(deck: Deck, cards: List<MTGCard>): Boolean {
        val deckFile = deck.fileNameForDeck() ?: return false
        val writer: OutputStreamWriter
        TrackingManager.trackDatabaseExport()
        try {
            writer = OutputStreamWriter(FileOutputStream(deckFile), "UTF-8")
            writer.append("//")
            writer.append(deck.name)
            writer.append("\n")
            for ((_, name, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, quantity, isSideboard) in cards) {
                if (isSideboard) {
                    writer.append("SB: ")
                }
                writer.append(quantity.toString())
                writer.append(" ")
                writer.append(name)
                writer.append("\n")
            }
            writer.flush()
            writer.close()
            return true
        } catch (e: IOException) {
            TrackingManager.trackDatabaseExportError(e.localizedMessage)
            return false
        }
    }
}

fun Deck.fileNameForDeck(): File? {
    val root = mtgSearchDirectory ?: return null
    return File(root, toDeckName() + ".dec")
}

private val mtgSearchDirectory: File?
    get() {
        val root = File(Environment.getExternalStorageDirectory(), if (BuildConfig.DEBUG) "MTGSearchDebug" else "MTGSearch")
        if (!root.exists()) {
            val created = root.mkdirs()
            if (!created) {
                return null
            }
        }
        return root
    }

fun Context.copyDbToSdCard(name: String): File? {
    LOG.e("copy db to sd card")
    try {
        val root = mtgSearchDirectory ?: return null
        if (root.canWrite()) {
            val currentDB = this.getDatabasePath(name)
            val backupDB = File(root, name)

            if (currentDB.exists()) {
                val src = FileInputStream(currentDB)
                        .channel
                val dst = FileOutputStream(backupDB)
                        .channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                return backupDB
            } else {
                LOG.e("current db dont exist")
            }
        } else {
            LOG.e("sd card cannot be write")
        }
    } catch (e: Exception) {
        LOG.e("exception copy db: " + e.localizedMessage)
    }

    return null
}

fun Context.copyDbFromSdCard(name: String): Boolean {
    LOG.e("copy db to sd card")
    try {
        val root = mtgSearchDirectory ?: return false
        if (root.canWrite()) {
            val currentDB = getDatabasePath(name)
            val backupDB = File(root, name)

            if (backupDB.exists()) {
                val src = FileInputStream(backupDB)
                        .channel
                val dst = FileOutputStream(currentDB)
                        .channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                return true
            } else {
                LOG.e("backup db dont exist")
            }
        } else {
            LOG.e("sd card cannot be write")
        }
    } catch (e: Exception) {
        LOG.e("exception copy db: " + e.localizedMessage)
    }

    return false
}