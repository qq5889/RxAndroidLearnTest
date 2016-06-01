package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

public class FilterActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected int layoutRes() {
        return R.layout.activity_filter;
    }

    /**
     * 防抖动操作，在debounce中的时间内，没有任何操作时，发射数据
     */
    @OnClick(R.id.btn_debounce)
    void onDebounceClick() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    for (int i = 0; i < 10; i++) {
                        subscriber.onNext(i);
                        Thread.sleep(500 * i);

                    }
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).debounce(2, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError:" + e.getLocalizedMessage() + " msg:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext:" + integer);
                    }
                });
    }

}
