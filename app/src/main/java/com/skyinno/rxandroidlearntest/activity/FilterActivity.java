package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

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


    /**
     * （过滤掉）重复的数据项
     * 结果 1 2 3 4 5 6
     */
    @OnClick(R.id.btn_distinct)
    void onDistinctClick() {
        Integer[] ints = {1, 2, 2, 3, 2, 4, 5, 6, 4, 5, 6};
        Observable.from(ints).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).distinct()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, integer + "");
                    }
                });
    }

    /**
     * （过滤掉）相邻两个重复的数据项
     * 结果 1 2 3 2 4 5 6 4 5 6
     */
    @OnClick(R.id.btn_distinctUntilChanged)
    void onDistinctUntilChanged() {
        Integer[] ints = {1, 2, 2, 3, 2, 4, 5, 6, 4, 4, 5, 5, 5, 5, 6};
        Observable.from(ints).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .distinctUntilChanged()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, integer + "");
                    }
                });
    }

    /**
     * 只发射第N项数据(从0开始)
     * 如果at(num) num为负数或者超出长度，则会抛出异常
     * <p>
     * 推荐使用elementAtOrDefault ， 当超出长度时，使用默认值 ， 当索引为负数的话 ，任然报错
     * 返回12
     * <p>
     * elementAtOrDefault(10,1) 返回1
     */
    @OnClick(R.id.btn_elementAt)
    void onElemeentAt() {
        Observable.range(3, 10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .elementAt(9)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, integer + "");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 过滤操作，只返回符合条件的内容
     */
    @OnClick(R.id.btn_filter)
    void onFilterClick() {
        Observable.range(4, 10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 8;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, integer + "");
                    }
                });
    }


    /**
     * 等同filter,只保留 是当前类型的数据
     * 显示数据 1,3
     */
    @OnClick(R.id.btn_ofType)
    void onOfTypeClick() {
        Observable.just("1", 2, "3", 4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .ofType(String.class)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, s);
                    }
                });
    }

    /**
     * 只发射第一个数据（第一个满足条件的数据）
     * <p>
     * -----[last,lastOrDefault等同，只不过是找最后一个，例子略]------
     * <p>
     * 当没有参数的时候或者通过过滤后没有数据的时候，将会抛出异常，需要引入onError
     * <p>
     * 推荐使用firstOrDefault ，增加一个默认值参数：当找不到数据的时候 ，返回的默认值
     * <p>
     * 或者使用takeFirst,用法等同first，但是不会抛出异常，直接进入completed
     * <p>
     * single忽略
     * <p>
     * 显示
     * 4
     * 7
     */
    @OnClick(R.id.btn_first)
    void onFirstClick() {
        Observable.range(4, 8).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .first()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "无过滤条件返回的第一个：" + integer);
                    }
                });

        Observable.range(4, 8).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .first(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 6;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "有过滤条件返回的第一个：" + integer);
            }
        });
    }

    /**
     * 不发射任何数据，只发射Observable的终止通知
     * 意思就是只会发个onCompleted或者onError,不关心onNext
     */
    @OnClick(R.id.btn_ignoreElement)
    void onIgnoreElement() {
        Observable.just(null).delay(2, TimeUnit.SECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .ignoreElements()
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError----" + e.getMessage());
                    }

                    @Override
                    public void onNext(Object integer) {
                        Log.i(TAG, "我是永远不会调用的_" + integer);
                    }
                });
    }

    /**
     * 抽样数据，在sample设定的某一时段内，发射在这个时间段内最后一个数据（别名 throttleLast）
     * <p>
     * throttleFirst于sample相似，只不过是发射这段时间内的第一个数据，案例类似，略
     * <p>
     * 如果自上次采样以来，原始Observable没有发射任何数据，这个操作返回的Observable在那段时间内也不会发射任何数据。
     */
    @OnClick(R.id.btn_sample)
    void onSampleClick() {
        Observable.interval(200, TimeUnit.MILLISECONDS).take(20).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .sample(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG, aLong + "");
                    }
                });
    }


    /**
     * 跳过前n个数据
     * 跳过前t长的时间中发射的数据
     * <p>
     * skipLast等同skip,只是跳过最后n个数据 ， 跳过最后t长时间内发射的数据
     */
    @OnClick(R.id.btn_skip)
    void onSkipClick() {
        Observable.interval(100, TimeUnit.MILLISECONDS).take(10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .skip(5)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG, aLong + "");
                    }
                });
    }

    /**
     * take只保留前多少个数据(或者多长时间内的数据)
     * <p>
     * takeLast 只发射后多少个数据（或者最后多长时间内的数据）
     * <p>
     * takeLastBuffer 将最后发射的多少个数据（或者最后多长时间内的数据）收集到一个list后再发射
     */
    @OnClick(R.id.btn_take)
    void onTakeClick() {
        Observable.range(4, 6).take(10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer aLong) {
                        Log.i(TAG, aLong + "");
                    }
                });
    }

}
