package com.dbottillo.mtgsearchfree.presenter;

public class RxWrapperFactory {


    public RxWrapperFactory() {

    }

    public RxWrapper singleWrapper() {
        return new RxWrapper<>();
    }

    public RxDoubleWrapper doubleWrapper() {
        return new RxDoubleWrapper<>();
    }

}
