package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider.getUriForFile
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.StringWriter
import javax.inject.Inject

class FileManager @Inject constructor(
    private val context: Context
) : FileManagerI {

    @Throws(FileNotFoundException::class)
    override fun loadUri(uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri) ?: throw FileNotFoundException("input stream is null")
    }

    @Throws(Resources.NotFoundException::class)
    override fun loadRaw(raw: Int): String {
        val inputStream = context.resources?.openRawResource(raw)
                ?: throw Resources.NotFoundException("impossible to open $raw")
        return loadFile(inputStream) ?: throw Resources.NotFoundException("impossible to open $raw")
    }

    @Throws(Resources.NotFoundException::class)
    private fun loadFile(input: InputStream): String? {

        val writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader = BufferedReader(InputStreamReader(input, "UTF-8"))
            var n: Int = reader.read(buffer)
            while (n != -1) {
                writer.write(buffer, 0, n)
                n = reader.read(buffer)
            }
            reader.close()
            input.close()
        } catch (e: IOException) {
            return null
        }

        return writer.toString()
    }

    @Throws(FileNotFoundException::class)
    override fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val path = File(context.filesDir, "images")
        if (!path.exists()) {
            val created = path.mkdirs()
            if (!created) {
                throw FileNotFoundException()
            }
        }
        val file = File(path, "artwork_share.jpg")
        val outputStream = FileOutputStream(file) as FileOutputStream?
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.close()
        return getUriForFile(context, "${context.packageName}.provider", file)
    }

    @Throws(FileNotFoundException::class)
    override fun saveDeckToFile(deck: Deck, cards: List<MTGCard>): Uri {
        val path = File(context.filesDir, "decks")
        if (!path.exists()) {
            val created = path.mkdirs()
            if (!created) {
                throw FileNotFoundException()
            }
        }
        val file = File(path, "deck_share.dec")
        val outputStream = FileOutputStream(file) as FileOutputStream?
        deck.toFile(outputStream, cards)
        outputStream?.close()
        return getUriForFile(context, "${context.packageName}.provider", file)
    }
}

interface FileManagerI {
    fun loadUri(uri: Uri): InputStream
    fun loadRaw(raw: Int): String
    fun saveBitmapToFile(bitmap: Bitmap): Uri
    fun saveDeckToFile(deck: Deck, cards: List<MTGCard>): Uri
}

private fun Deck.toFile(fileOutputStream: FileOutputStream?, cards: List<MTGCard>) {
    TrackingManager.trackDatabaseExport()
    val writer = OutputStreamWriter(fileOutputStream, "UTF-8")
    writer.append("//")
    writer.append(name)
    writer.append("\n")
    for (card in cards) {
        if (card.isSideboard) {
            writer.append("SB: ")
        }
        writer.append(card.quantity.toString())
        writer.append(" ")
        writer.append(card.name)
        writer.append("\n")
    }
    writer.flush()
    writer.close()
}