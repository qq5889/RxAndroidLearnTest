package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

public class FunctionActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_function;
    }


    /**
     * 延迟一秒后发射数据
     */
    @OnClick(R.id.btn_delay)
    void onDelayClick() {
        Observable.range(1, 4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .delay(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, integer + "");
                    }
                });
    }

    /**
     * 延迟订阅原始Observable
     * todo 没搞明白和delay的区别
     */
    @OnClick(R.id.btn_delaySubscribtion)
    void onDelaySubscribtionClick() {
        Observable.range(1, 4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .delaySubscription(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, integer + "");
                    }
                });
    }


    /**
     * Do操作符是在Observable的生命周期的各个阶段加上一系列的回调监听
     * <p>
     * DoOnEach --- Observable每发射一个数据的时候就会触发这个回调，不仅包括onNext还包括onError和onCompleted。
     * <p>
     * DoOnNext --- 只有onNext的时候才会被触发
     * <p>
     * DoOnError --- 只有onError发生的时候触发回调
     * <p>
     * DoOnComplete --- 只有onComplete发生的时候触发回调
     * <p>
     * DoOnSubscribe和DoOnUnSubscribe --- 在Subscriber进行订阅和反订阅的时候触发回调
     * <p>
     * DoOnTerminate --- 在Observable结束前触发回调，无论是正常还是异常终止
     * <p>
     * doAfterTerminate --- 在Observable结束后触发回调，无论是正常还是异常终止
     * <p>
     * <p>
     * 结果：
     * onStart
     * doOnSubscribe:在Subscriber进行订阅的时候触发回调
     * doOnEach:每发射一个数据的时候就会触发这个回调，包括onNext/onError/onCompleted
     * doOnNext:只有onNext的时候才会被触发 : num:1
     * onNext:1
     * doOnEach:每发射一个数据的时候就会触发这个回调，包括onNext/onError/onCompleted
     * doOnNext:只有onNext的时候才会被触发 : num:2
     * onNext:2
     * doOnEach:每发射一个数据的时候就会触发这个回调，包括onNext/onError/onCompleted
     * doOnNext:只有onNext的时候才会被触发 : num:3
     * onNext:3
     * doOnEach:每发射一个数据的时候就会触发这个回调，包括onNext/onError/onCompleted
     * doOnCompleted:只有onComplete发生的时候触发回调
     * doOnTerminate:在Observable结束前触发回调，无论是正常还是异常终止
     * onCompleted
     * doOnUnsubscribe:在Subscriber进行反订阅的时候触发回调
     * doAfterTerminate:在Observable结束后触发回调，无论是正常还是异常终止
     */
    @OnClick(R.id.btn_do)
    void OnDoClick() {
        Observable.range(1, 3).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .doOnEach(new Action1<Notification<? super Integer>>() {
                    @Override
                    public void call(Notification<? super Integer> notification) {
                        Log.i(TAG, "doOnEach:每发射一个数据的时候就会触发这个回调，包括onNext/onError/onCompleted");
                    }
                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "doOnNext:只有onNext的时候才会被触发 : num:" + integer);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, "DoOnError:只有onError发生的时候触发回调 : error:" + throwable.getMessage());
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "doOnCompleted:只有onComplete发生的时候触发回调");
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "doOnSubscribe:在Subscriber进行订阅的时候触发回调");
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "doOnUnsubscribe:在Subscriber进行反订阅的时候触发回调");
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "doOnTerminate:在Observable结束前触发回调，无论是正常还是异常终止");
                    }
                }).doAfterTerminate(new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "doAfterTerminate:在Observable结束后触发回调，无论是正常还是异常终止");
            }
        })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.i(TAG, "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext:" + integer);
                    }
                });
    }

    /**
     * 将数据项和事件通知都当做数据项发射（意思就是onnext的数据和onCompleted的数据都会调用到call里）
     * <p>
     * deMaterialize是materialize的逆向工程 例子略
     * <p>
     * 结果：
     * OnNext : 1
     * OnNext : 2
     * OnNext : 3
     * OnCompleted : null
     */
    @OnClick(R.id.btn_materialize)
    void onMaterializeClick() {
        Observable.range(1, 3).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .materialize()
                .subscribe(new Action1<Notification<Integer>>() {
                    @Override
                    public void call(Notification<Integer> integerNotification) {
                        Log.i(TAG, integerNotification.getKind() + " : " + integerNotification.getValue());
                    }
                });
    }

    /**
     * 操作符拦截原始Observable发射的数据项，替换为发射表示相邻发射物时间间隔的对象。这个数据包含当前的数据和延迟的时间
     */
    @OnClick(R.id.btn_timeInterval)
    void onTimeIntervalClick() {
        Observable.interval(1, TimeUnit.SECONDS).take(4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .timeInterval()
                .subscribe(new Action1<TimeInterval<Long>>() {
                    @Override
                    public void call(TimeInterval<Long> longTimeInterval) {
                        Log.i(TAG, longTimeInterval.getValue() + " " + longTimeInterval.getIntervalInMilliseconds());
                    }
                });
    }

    /**
     * 操作符拦截原始Observable发射的数据项，会将每个数据项给重新包装一下，加上了一个时间戳来标明每次发射的时间。这个数据包含当前的数据和时间戳
     */
    @OnClick(R.id.btn_timeStamp)
    void onTimeStampClick() {
        Observable.interval(1, TimeUnit.SECONDS).take(4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .timestamp()
                .subscribe(new Action1<Timestamped<Long>>() {
                    @Override
                    public void call(Timestamped<Long> longTimestamped) {
                        Log.i(TAG, longTimestamped.getValue() + " " + longTimestamped.getTimestampMillis());
                    }
                });
    }

    /**
     * Timeout操作符给Observable加上超时时间，每发射一个数据后就重置计时器，当超过预定的时间还没有发射下一个数据，就抛出一个超时的异常。
     */
    @OnClick(R.id.btn_timeOut)
    void onTimeOutClick() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(i * 1000);
                        subscriber.onNext(i);
                    }

                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        }).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())

                .timeout(3, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext:" + integer);
                    }
                });
    }
}
