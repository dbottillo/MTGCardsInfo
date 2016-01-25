package com.dbottillo.mtgsearchfree.communication.events;

public class ErrorEvent extends BaseEvent<String> {

    public ErrorEvent(String result) {
        this.result = result;
    }
}
