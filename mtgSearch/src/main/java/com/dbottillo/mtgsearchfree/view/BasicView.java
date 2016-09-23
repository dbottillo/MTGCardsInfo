package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.exceptions.MTGException;

public interface BasicView {

    void showError(String message);

    void showError(MTGException exception);

}


