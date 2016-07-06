package com.dbottillo.mtgsearchfree.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

@SuppressWarnings("SimplifiableIfStatement")
public final class PermissionUtil {

    private PermissionUtil() {

    }

    public static boolean permissionGranted(Context context, TYPE type) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        return context.checkSelfPermission(type.permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, TYPE type) {
        ActivityCompat.requestPermissions(activity, new String[]{type.permission}, 1);
    }

    public enum TYPE {
        READ_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
        WRITE_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        String permission;

        TYPE(String permission) {
            this.permission = permission;
        }
    }

    public interface PermissionListener {
        void permissionGranted();

        void permissionNotGranted();
    }

    public static boolean isGranted(int[] grantResults) {
        return grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
