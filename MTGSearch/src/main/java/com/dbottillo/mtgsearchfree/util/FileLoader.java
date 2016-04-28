package com.dbottillo.mtgsearchfree.util;

import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FileLoader {

    InputStream loadUri(Uri uri) throws FileNotFoundException;
}
