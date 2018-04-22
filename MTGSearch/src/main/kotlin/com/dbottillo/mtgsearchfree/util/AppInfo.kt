package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.content.pm.PackageManager

class AppInfo(private val context: Context) {

    val firstInstallTime: Long
        get() {
            return try {
                context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            } catch (ignored: PackageManager.NameNotFoundException) {
                -1
            }

        }

    val lastUpdateTime: Long
        get() {
            return try {
                context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
            } catch (ignored: PackageManager.NameNotFoundException) {
                -1
            }

        }
}
