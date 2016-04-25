package com.dbottillo.mtgsearchfree.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class PermissionUtil {

    public static boolean storageGranted(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public static boolean isGranted(int[] grantResults) {
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
