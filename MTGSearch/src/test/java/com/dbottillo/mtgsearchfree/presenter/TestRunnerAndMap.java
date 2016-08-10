package com.dbottillo.mtgsearchfree.presenter;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class TestRunnerAndMap<T, K> extends RunnerAndMap<T, K> {

    @Override
    Subscription runAndMap(Observable<T> on, Func1<T, K> mapFun, final RxWrapperListener<K> listener) {
        K data = on.map(mapFun).toBlocking().first();
        if (listener != null) {
            listener.onNext(data);
        }
        return null;
    }
}
