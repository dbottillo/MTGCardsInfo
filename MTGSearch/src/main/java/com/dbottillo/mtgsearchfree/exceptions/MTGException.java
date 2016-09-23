package com.dbottillo.mtgsearchfree.exceptions;

import android.content.Context;

public class MTGException extends Exception {

    private ExceptionCode code;
    private String message;

    public MTGException(ExceptionCode code, String message){
        this.code = code;
        this.message = message;
    }

    public String getLocalizedMessage(Context context){
        return context.getResources().getString(code.getResource());
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ExceptionCode getCode() {
        return code;
    }
}
