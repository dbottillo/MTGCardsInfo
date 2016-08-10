package com.dbottillo.mtgsearchfree.presenter;

public class TestRunnerFactory extends RunnerFactory {

    public TestRunnerFactory() {

    }

    public <T> Runner<T> simple() {
        return new TestRunner<>();
    }

    public <T,K> RunnerAndMap<T,K> withMap() {
        return new TestRunnerAndMap<>();
    }
}
