package com.dbottillo.mtgsearchfree.util

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
}
