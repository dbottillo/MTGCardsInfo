package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.FileProvider.getUriForFile
import com.dbottillo.mtgsearchfree.BuildConfig
import java.io.*
import javax.inject.Inject

class FileManager @Inject constructor(private val context: Context) : FileManagerI {

    @Throws(FileNotFoundException::class)
    override fun loadUri(uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri)
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
        return getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    }
}

interface FileManagerI{
    fun loadUri(uri: Uri): InputStream
    fun loadRaw(raw: Int): String
    fun saveBitmapToFile(bitmap: Bitmap): Uri
}