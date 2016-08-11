package com.dbottillo.mtgsearchfree.presenter;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RunnerAndMap<T, K> extends Runner<T> {

    Subscription runAndMap(Observable<T> on, Func1<T, K> mapFun, final RxWrapperListener<K> listener) {
        Observable<K> obs = on.observeOn(AndroidSchedulers.mainThread())
                .map(mapFun).subscribeOn(Schedulers.io());
        return obs.subscribe(new Observer<K>() {
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
            public void onNext(K data) {
                if (listener != null) {
                    listener.onNext(data);
                }
            }
        });
    }
}
