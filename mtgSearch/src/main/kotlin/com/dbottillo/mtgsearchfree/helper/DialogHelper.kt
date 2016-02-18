package com.dbottillo.mtgsearchfree.helper

import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity

class DialogHelper {

    companion object {
        fun open(activity: AppCompatActivity, tag: String, fragment: DialogFragment) {
            val ft = activity.supportFragmentManager.beginTransaction()
            val prev = activity.supportFragmentManager.findFragmentByTag(tag)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            fragment.show(ft, tag)
        }
    }
}

