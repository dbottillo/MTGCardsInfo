package com.dbottillo.mtgsearchfree.presenter;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

class TestRunner<T> extends Runner<T> {

    @Override
    public void run(Observable<T> on, final RxWrapperListener<T> listener) {
        on.blockingSubscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T t) {
                if (listener != null) {
                    listener.onNext(t);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
