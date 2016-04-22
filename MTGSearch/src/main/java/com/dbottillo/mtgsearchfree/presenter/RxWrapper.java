package com.dbottillo.mtgsearchfree.presenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxWrapper<T> {

    interface RxWrapperListener<T> {
        void onNext(T data);

        void onError(Throwable e);

        void onCompleted();
    }

    Subscription run(Observable<T> on, final RxWrapperListener<T> listener) {
        on.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        return on.subscribe(new Observer<T>() {
            @Override
            public void onCompleted() {
                if (listener != null) {
                    listener.onCompleted();
                }

            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }

            @Override
            public void onNext(T data) {
                if (listener != null) {
                    listener.onNext(data);
                }
            }
        });
    }
}
