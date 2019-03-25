package com.dbottillo.mtgsearchfree.util

import com.dbottillo.mtgsearchfree.core.BuildConfig

class Logger {

    fun d(message: String) {
        LOG.d(message)
    }

    fun d() {
        LOG.d()
    }

    fun e(throwable: Throwable) {
        LOG.e(throwable.localizedMessage)
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
            LOG.d(message)
        }
    }
}
