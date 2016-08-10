package com.dbottillo.mtgsearchfree.presenter;

public class RunnerFactory {


    public RunnerFactory() {

    }

    public <T> Runner<T> simple() {
        return new Runner<>();
    }

    public <T,K> RunnerAndMap<T,K> withMap() {
        return new RunnerAndMap<>();
    }

}
