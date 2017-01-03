package com.dbottillo.mtgsearchfree.presenter;


import io.reactivex.Observable;
import io.reactivex.functions.Function;

class TestRunnerAndMap<T, K> extends RunnerAndMap<T, K> {

    @Override
    void runAndMap(Observable<T> on, Function<T, K> mapFun, final RxWrapperListener<K> listener) {
        K data = on.map(mapFun).blockingFirst();
        if (listener != null) {
            listener.onNext(data);
        }
    }
}
