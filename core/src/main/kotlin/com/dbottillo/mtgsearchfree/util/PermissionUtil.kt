package com.dbottillo.mtgsearchfree.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionUtil {

    fun permissionGranted(context: Context, permission: PermissionAvailable): Boolean {
        return context.checkSelfPermission(permission.value) == PackageManager.PERMISSION_GRANTED
    }

    interface PermissionListener {
        fun permissionGranted()

        fun permissionNotGranted()
    }

    fun isGranted(grantResults: IntArray): Boolean {
        return grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.request(permission: PermissionAvailable) {
    ActivityCompat.requestPermissions(this, arrayOf(permission.value), 1)
}

sealed class PermissionAvailable(val value: String) {
    object ReadStorage : PermissionAvailable(Manifest.permission.READ_EXTERNAL_STORAGE)
    object WriteStorage : PermissionAvailable(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}