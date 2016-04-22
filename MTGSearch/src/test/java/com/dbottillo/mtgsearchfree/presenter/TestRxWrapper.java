package com.dbottillo.mtgsearchfree.presenter;

import rx.Observable;
import rx.Subscription;

public class TestRxWrapper<T> extends RxWrapper<T> {

    @Override
    Subscription run(Observable<T> on, RxWrapperListener<T> listener) {
        T data = on.toBlocking().first();
        if (listener != null) {
            listener.onNext(data);
        }
        return null;
    }
}
