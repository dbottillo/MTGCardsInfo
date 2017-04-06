package com.dbottillo.mtgsearchfree.presenter;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Runner<T> {

    public interface RxWrapperListener<T> {
        void onNext(T data);

        void onError(Throwable e);

        void onCompleted();
    }

    public void run(Observable<T> on, final RxWrapperListener<T> listener) {
        on.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        on.subscribe(new Observer<T>() {
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
            public void onNext(T data) {
                if (listener != null) {
                    listener.onNext(data);
                }
            }
        });

    }
}
