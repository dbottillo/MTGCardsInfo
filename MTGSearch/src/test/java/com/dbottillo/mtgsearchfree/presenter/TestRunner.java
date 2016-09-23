package com.dbottillo.mtgsearchfree.presenter;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class TestRunner<T> extends Runner<T> {

    @Override
    Subscription run(Observable<T> on, final RxWrapperListener<T> listener) {
        on.toBlocking().subscribe(new Subscriber<T>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                listener.onError(e);
            }

            @Override
            public void onNext(T t) {
                if (listener != null) {
                    listener.onNext(t);
                }
            }
        });
        return null;
    }
}
