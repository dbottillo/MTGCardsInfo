package com.dbottillo.mtgsearchfree.presenter;

public class TestRxWrapperFactory extends RxWrapperFactory {

    public TestRxWrapperFactory() {

    }

    public RxWrapper singleWrapper() {
        return new TestRxWrapper<>();
    }

    public RxDoubleWrapper doubleWrapper() {
        return new TestRxDoubleWrapper<>();
    }
}
