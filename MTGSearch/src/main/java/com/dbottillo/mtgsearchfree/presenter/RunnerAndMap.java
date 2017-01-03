package com.dbottillo.mtgsearchfree.presenter;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RunnerAndMap<T, K> extends Runner<T> {

    void runAndMap(Observable<T> on, Function<T, K> mapFun, final RxWrapperListener<K> listener) {
        Observable<K> obs = on.observeOn(AndroidSchedulers.mainThread())
                .map(mapFun).subscribeOn(Schedulers.io());
        obs.subscribe(new Observer<K>() {

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }

            @Override
            public void onComplete() {
                if (listener != null) {
                    listener.onCompleted();
                }
            }

            @Override
            public void onSubscribe(Disposable d) {

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
