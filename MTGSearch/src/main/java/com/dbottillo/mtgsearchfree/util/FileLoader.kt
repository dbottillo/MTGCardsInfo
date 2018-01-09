package com.dbottillo.mtgsearchfree.util

import android.content.res.Resources
import android.net.Uri
import java.io.FileNotFoundException
import java.io.InputStream

interface FileLoader {

    @Throws(FileNotFoundException::class)
    fun loadUri(uri: Uri): InputStream

    @Throws(Resources.NotFoundException::class)
    fun loadRaw(raw: Int): String
}
