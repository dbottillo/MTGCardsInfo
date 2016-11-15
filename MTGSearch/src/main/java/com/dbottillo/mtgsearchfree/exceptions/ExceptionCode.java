package com.dbottillo.mtgsearchfree.exceptions;

import com.dbottillo.mtgsearchfree.R;

public enum ExceptionCode {
    DECK_NOT_IMPORTED(R.string.deck_not_imported);

    private int resource;

    ExceptionCode(int resource){
        this.resource =resource;
    }

    public int getResource() {
        return resource;
    }
}
