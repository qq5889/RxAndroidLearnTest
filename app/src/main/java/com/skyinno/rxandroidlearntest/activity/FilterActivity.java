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
     *
     * 推荐使用elementAtOrDefault ， 当超出长度时，使用默认值 ， 当索引为负数的话 ，任然报错
     * 返回12
     *
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
                        Log.i(TAG,throwable.getMessage());
                    }
                });
    }

    /**
     * 过滤操作，只返回符合条件的内容
     */
    @OnClick(R.id.btn_filter)
    void onFilterClick(){
        Observable.range(4,10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer>8;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG,integer+"");
                    }
                });
    }


    /**
     * 等同filter,只保留 是当前类型的数据
     * 显示数据 1,3
     */
    @OnClick(R.id.btn_ofType)
    void onOfTypeClick(){
        Observable.just("1",2,"3",4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .ofType(String.class)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG,s);
                    }
                });
    }
}
