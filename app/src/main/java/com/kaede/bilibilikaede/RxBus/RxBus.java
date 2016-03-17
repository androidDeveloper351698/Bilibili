package com.kaede.bilibilikaede.RxBus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by asus on 2016/2/3.
 *
 */
public class RxBus {
    private SerializedSubject<Object,Object> subject = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o){
        subject.onNext(o);
    }

    public Observable<Object> toObservable(){
        return subject;
    }

    public boolean hasObservers(){
        return subject.hasObservers();
    }
}
