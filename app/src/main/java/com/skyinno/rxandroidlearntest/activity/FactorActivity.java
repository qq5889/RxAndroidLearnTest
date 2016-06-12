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
import rx.functions.Func2;

public class FactorActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_factor;
    }

    /**
     * 判定是否Observable发射的所有数据都满足某个条件
     */
    @OnClick(R.id.btn_all)
    void onAllClick() {
        Observable.range(0, 10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .all(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer < 5;
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.i(TAG, "数据是否全部满足条件：" + aBoolean);

                    }
                });
    }

    /**
     * 当你传递多个Observable给Amb时，它只发射首先发送通知给Amb的那个，不管发射的是一项数据还是一个onError或onCompleted通知。
     * Amb将忽略和丢弃其它所有Observables的发射物。
     * <p>
     * Observable.amb(o1,o2)和o1.ambWith(o2)是等价的。
     */
    @OnClick(R.id.btn_amb)
    void onAmbClick() {
        Observable<Integer> o1 = Observable.range(10, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());
        Observable<Integer> o2 = Observable.range(1, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).delay(100, TimeUnit.MILLISECONDS);

        Observable.amb(o1, o2)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, integer + "");
                    }
                });
    }

    /**
     * 给Contains传一个指定的值，如果原始Observable发射了那个值，它返回的Observable将发射true，否则发射false
     * <p>
     * 遇到第一个存在的就返回结果
     */
    @OnClick(R.id.btn_contains)
    void onContainsClick() {
        Observable.interval(1, TimeUnit.SECONDS).take(5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        Log.i(TAG, "map:" + aLong);
                        return aLong;
                    }
                })
                .contains(1L)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.i(TAG, "数据中是否存在1：" + aBoolean);
                    }

                });
    }

    /**
     * 只要任何一项满足条件就返回一个发射true的Observable，否则返回一个发射false的Observable。
     * <p>
     * 他会立即返回结果
     */
    @OnClick(R.id.btn_exists)
    void onExistsClick() {
        Observable.interval(1, TimeUnit.SECONDS).take(5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .exists(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long integer) {
                        return integer < 2;
                    }
                })
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.i(TAG, "数据中是否存在满足小于2的数据：" + aBoolean);
                    }
                });
    }

    /**
     * 判定原始Observable是否没有发射任何数据
     */
    @OnClick(R.id.btn_isEmpty)
    void onIsEmptyClick() {
        Observable.range(0, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 10;
                    }
                })
                .isEmpty()
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.i(TAG, "数据是否为空：" + aBoolean);
                    }
                });
    }

    /**
     * 当发射数据为空的时候，发射一个默认值
     * <p>
     * 如果不为空，则发射原始数据
     */
    @OnClick(R.id.btn_defaultEmpty)
    void onDefaultIfEmptyClick() {
        Observable.range(0, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 10;
                    }
                })
                .defaultIfEmpty(100)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "" + integer);
                    }
                });

    }

    /**
     * 当发射的数据为空时，将发射一个新的备用Observable的发射物
     */
    @OnClick(R.id.btn_switchIfEmpty)
    void onSwitchIfEmptyClick() {
        Observable<Integer> o2 = Observable.range(10, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());


        Observable.range(0, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 10;
                    }
                })
                .switchIfEmpty(o2)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "" + integer);
                    }
                });

    }

    /**
     * 比较两个Observable的发射物
     * 如果两个序列是相同的（相同的数据，相同的顺序，相同的终止状态），它就发射true，否则发射false。
     */
    @OnClick(R.id.btn_sequenceEqual)
    void onSequenceEqualClick() {
        Observable<Integer> o1 = Observable.range(0, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());
        Observable<Long> o2 = Observable.interval(100, TimeUnit.MILLISECONDS).take(5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());

        Observable.sequenceEqual(o1, o2, new Func2<Number, Number, Boolean>() {
            @Override
            public Boolean call(Number number, Number number2) {
                return number.intValue() == number2.intValue();
            }
        })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Log.i(TAG, "是否是相同的两个发射物：" + aBoolean);
                    }
                });
    }

    /**
     * 跳过原始数据，直到新的Observable发射了数据，则开始发射原始数据
     */
    @OnClick(R.id.btn_skipUntil)
    void onSkipUntilClick() {
        Observable<Integer> o1 = Observable.range(0, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).delay(200,TimeUnit.MILLISECONDS);

        Observable.interval(100, TimeUnit.MILLISECONDS).take(5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .skipUntil(o1)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG,aLong+"");
                    }
                });
    }

    /**
     * 跳过原始数据，直到当满足了条件后，则开始发射原始数据
     */
    @OnClick(R.id.btn_skipWhile)
    void onSkipWhileClick() {

        Observable.interval(100, TimeUnit.MILLISECONDS).take(5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .skipWhile(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong==2;
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG,aLong+"");
                    }
                });
    }

    /**
     * 如果第二个Observable发射了一项数据或者发射了一个终止通知，TakeUntil返回的Observable会停止发射原始Observable并终止。
     */
    @OnClick(R.id.btn_takeUntil)
    void onTakeUntilClick() {
        Observable<Integer> o1 = Observable.range(0, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).delay(200,TimeUnit.MILLISECONDS);

        Observable.interval(100, TimeUnit.MILLISECONDS).take(5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .takeUntil(o1)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG,aLong+"");
                    }
                });
    }

    /**
     *  当满足了条件或者发射完数据时，停止发射原始数据，并终止。
     */
    @OnClick(R.id.btn_takeWhile)
    void onTakeWhileClick() {

        Observable.interval(100, TimeUnit.MILLISECONDS).take(5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .takeWhile(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong==2;
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.i(TAG,aLong+"");
                    }
                });
    }
}
