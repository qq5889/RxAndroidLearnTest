package com.skyinno.rxandroidlearntest.activity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SimpleRxActivity extends BaseActivity {

    private String TAG = getClass().getSimpleName();
    @Bind(R.id.btn_create)
    Button mBtnCreate;
    @Bind(R.id.btn_just)
    Button mBtnJust;
    @Bind(R.id.btn_from)
    Button mBtnFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_rx);
        ButterKnife.bind(this);
    }

    private void checkMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.i(TAG, "______________mainThread");
        } else {
            Log.i(TAG, "not____________mainThread");
        }
    }

    @OnClick({R.id.btn_create, R.id.btn_just, R.id.btn_from})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                create();
                break;
            case R.id.btn_just:
                just();
                break;
            case R.id.btn_from:
                from();
                break;

        }
    }

    private void create() {
        Observable
                .create(new Observable.OnSubscribe<String>() {  // 执行顺序 先走调用onStart,再调用call
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        checkMainThread();
                        subscriber.onNext("next_1");
                        subscriber.onNext("next_2");
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        checkMainThread();
                        Log.i(TAG, "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        checkMainThread();
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError");
                    }

                    @Override
                    public void onNext(String s) {
                        checkMainThread();
                        Log.i(TAG, "onNext:" + s);
                    }
                });
    }

    private void just() {
        Observable.just("next_1", "next_2").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                checkMainThread();
                Log.i(TAG, s);
            }
        });
    }

    private void from() {
        String[] ss = new String[]{"next_1", "next_2"};
        Observable.from(ss).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                checkMainThread();
                Log.i(TAG, s);
            }
        });

    }

    /**
     * Interval操作符返回一个Observable，它按固定的时间间隔发射一个无限递增的整数序列。
     */
    @OnClick(R.id.btn_interval)
    void onIntervalClick() {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .limit(11)
                .compose(RxLifecycle.bindLifecycle(this))
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return 10 - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG, aLong + "");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, "throwable");
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "over");
                    }
                });
    }


    /**
     * Range操作符发射一个范围内的有序整数序列，你可以指定范围的起始和长度。

     RxJava将这个操作符实现为range函数，它接受两个参数，一个是范围的起始值，一个是范围的数据的数目。如果你将第二个参数设为0，将导致Observable不发射任何数据（如果设置为负数，会抛异常）。

     range默认不在任何特定的调度器上执行。有一个变体可以通过可选参数指定Scheduler。

     Javadoc: range(int,int))
     Javadoc: range(int,int,Scheduler))

     */
    @OnClick(R.id.btn_range)
    void onRangeClick(){
        Observable.range(10,8).compose(RxLifecycle.bindLifecycle(this)).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG,integer+"");
            }
        });

    }
}
