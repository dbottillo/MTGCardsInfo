package com.dbottillo.mtgsearchfree.communication.events;

import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

public class SetEvent extends BaseEvent<ArrayList<MTGSet>> {

    public SetEvent(ArrayList<MTGSet> result) {
        this.result = result;
    }
}
