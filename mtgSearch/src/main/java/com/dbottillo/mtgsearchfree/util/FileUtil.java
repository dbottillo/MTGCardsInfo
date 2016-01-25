package com.dbottillo.mtgsearchfree.util;

import android.os.Environment;

import com.dbottillo.mtgsearchfree.helper.LOG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public final class FileUtil {

    private FileUtil() {
    }

    public static void copyDbToSdcard(String name) {
        LOG.e("copy db to sd card");
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/com.dbottillo.mtgsearchfree.debug/databases/" + name;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, name);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } else {
                    LOG.e("current db dont exist");
                }
            } else {
                LOG.e("sd card cannot be write");
            }
        } catch (Exception e) {
            LOG.e("exception copy db: " + e.getLocalizedMessage());
        }
    }
}
