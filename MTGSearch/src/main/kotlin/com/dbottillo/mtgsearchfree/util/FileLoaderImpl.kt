package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import java.io.*

class FileLoaderImpl(private val context: Context) : FileLoader {

    @Throws(FileNotFoundException::class)
    override fun loadUri(uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri)
    }

    @Throws(Resources.NotFoundException::class)
    override fun loadRaw(raw: Int): String {
        val inputStream = context.resources?.openRawResource(raw) ?: throw Resources.NotFoundException("impossible to open $raw")
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

}
