package com.dbottillo.mtgsearchfree.exceptions

import android.content.Context

class MTGException(val code: ExceptionCode, message: String) : Exception(message) {

    fun getLocalizedMessage(context: Context): String {
        return context.resources.getString(code.resource)
    }
}
