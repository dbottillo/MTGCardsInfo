package com.dbottillo.communication.events;

import com.dbottillo.communication.DataManager;

public abstract class BaseEvent<T> {

    private DataManager.TASK task;
    private boolean error;
    private String errorMessage;
    protected T result;

    public BaseEvent(boolean error, String message) {
        this.error = error;
        this.errorMessage = message;
    }

    protected BaseEvent() {
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getResult() {
        return result;
    }

    public DataManager.TASK getTask() {
        return task;
    }

    public void setTask(DataManager.TASK task) {
        this.task = task;
    }

}
