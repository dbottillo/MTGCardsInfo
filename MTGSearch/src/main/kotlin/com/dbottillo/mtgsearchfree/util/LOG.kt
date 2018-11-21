package com.dbottillo.mtgsearchfree.util

import android.app.Instrumentation
import android.text.TextUtils
import android.util.Log

import com.dbottillo.mtgsearchfree.BuildConfig
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.google.gson.GsonBuilder

object LOG {

    private const val TAG = "MTG"

    private val NOT_FOUND = StackTraceElement("", "", "", 0)

    private fun determineCaller(): StackTraceElement {
        var validElement = NOT_FOUND
        for (element in RuntimeException().stackTrace) {
            if (element.className != LOG::class.java.name &&
                    element.className != BasicActivity::class.java.name &&
                    element.className != BasicFragment::class.java.name &&
                    element.className != Logger::class.java.name &&
                    element.className != Instrumentation::class.java.name) {
                validElement = element
                break
            }
        }
        return validElement
    }

    private fun getClassNameOnly(classNameWithPackage: String): String {
        val lastDotPos = classNameWithPackage.lastIndexOf('.')
        return if (lastDotPos == -1) {
            classNameWithPackage
        } else classNameWithPackage.substring(lastDotPos + 1)
    }

    private fun enhanced(message: String?): String? {
        if (!BuildConfig.DEBUG) {
            return message
        }
        val caller = determineCaller()
        val classNameOnly = getClassNameOnly(caller.className)
        val methodName = caller.methodName
        val lineNumber = caller.lineNumber
        if (BuildConfig.LOG_THREAD) {
            val thread = Thread.currentThread()
            return String.format("%s [%s:%s:%s] %s", message, classNameOnly, methodName, lineNumber, thread)
        }
        return if (message == null || TextUtils.isEmpty(message)) {
            String.format("=== %s:%s:%s", classNameOnly, methodName, lineNumber)
        } else String.format("=== %s:%s:%s ->  %s", classNameOnly, methodName, lineNumber, message)
    }

    fun d(msg: String = "") {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, enhanced(msg))
        }
    }

    fun v(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, enhanced(msg))
        }
    }

    fun e(message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, enhanced(message))
        }
    }

    fun e(e: Exception) {
        e(e.message ?: "")
    }

    fun query(query: String, vararg params: String) {
        if (BuildConfig.DEBUG) {
            var message = query
            if (params.isNotEmpty()) {
                message += " with param: "
            }
            for (param in params) {
                message += "$param "
            }
            d(message)
        }
    }

    fun dump(o: Any?) {
        if (BuildConfig.DEBUG) {
            try {
                if (o == null) {
                    d("Object is null")
                } else {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    d("" + gson.toJson(o))
                }
            } catch (e: Exception) {
                d("Error dumping object: " + e.localizedMessage)
            }
        }
    }
}
