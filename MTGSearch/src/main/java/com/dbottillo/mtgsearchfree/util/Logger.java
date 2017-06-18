package com.dbottillo.mtgsearchfree.util;

public class Logger {

    public Logger(){

    }

    public void d(String message) {
        LOG.d(message);
    }

    public void d(){
        LOG.d();
    }

    public void e(Throwable throwable) {
        LOG.e(throwable.getLocalizedMessage());
    }
}
