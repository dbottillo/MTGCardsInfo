package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileLoaderImpl implements FileLoader {

    private Context context;

    public FileLoaderImpl(Context context) {
        this.context = context;
    }

    @Override
    public InputStream loadUri(Uri uri) throws FileNotFoundException {
        return context.getContentResolver().openInputStream(uri);
    }
}
