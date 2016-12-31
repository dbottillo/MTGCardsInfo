package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class AppInfo {

    private final Context context;

    public AppInfo(Context context) {
        this.context = context;
    }

    public long getFirstInstallTime(){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime;
        } catch (PackageManager.NameNotFoundException ignored) {
            return -1;
        }
    }

    public long getLastUpdateTime(){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).lastUpdateTime;
        } catch (PackageManager.NameNotFoundException ignored) {
            return -1;
        }
    }
}
