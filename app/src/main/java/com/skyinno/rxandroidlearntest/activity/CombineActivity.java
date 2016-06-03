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

public class CombineActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_combine;
    }

    /**
     * 结合两条时间线上最近的数据（最近的左边和最近的右边）不重复，意思就是不会出现 1 2,2 1的情况
     */
    @OnClick(R.id.btn_combineLatest)
    void onCombineLatestClick() {
        Observable<Long> o1 = Observable.interval(300, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(6);
        Observable<Long> o2 = Observable.interval(200, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(9);

        Observable.combineLatest(o1, o2, new Func2<Long, Long, String>() {
            @Override
            public String call(Long integer, Long integer2) {
                return integer + " " + integer2;
            }
        })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, s);
                    }
                });
    }


    /**
     * 与combineLatest相似
     * 但是join是结合在每个Observable的生命周期函数中的数据，可能会出现交替结合的情况  比如 1 2,2 1
     */
    @OnClick(R.id.btn_join)
    void onJoinClick() {
        Observable<Long> o1 = Observable.interval(300, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(6);
        Observable<Long> o2 = Observable.interval(200, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(9);
        o1.join(o2,
                new Func1<Long, Observable<Long>>() { // o1的 生命周期 的控制函数 ，这里意思是存活200毫秒
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.just(aLong).delay(200, TimeUnit.MILLISECONDS);
                    }
                }, new Func1<Long, Observable<Long>>() { // o2的 生命周期 的控制函数，这里意思是存活300毫秒
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.just(aLong).delay(300, TimeUnit.MILLISECONDS);
                    }
                }, new Func2<Long, Long, String>() { //结果合并规则
                    @Override
                    public String call(Long aLong, Long aLong2) {
                        return aLong + " " + aLong2;
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i(TAG, s);
            }
        });
    }

    /**
     * GroupJoin操作符的使用与join操作符和相似，区别在于第四个参数传入的函数不同
     */
    @OnClick(R.id.btn_groupJoin)
    void onGroupJoinClick() {
        Observable<Long> o1 = Observable.interval(300, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(6);
        Observable<Long> o2 = Observable.interval(200, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(9);
        o1.groupJoin(o2
                , new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.just(aLong).delay(200, TimeUnit.MILLISECONDS);
                    }
                }
                , new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.just(aLong).delay(300, TimeUnit.MILLISECONDS);
                    }
                }
                , new Func2<Long, Observable<Long>, Observable<String>>() {

                    @Override
                    public Observable<String> call(Long aLong, Observable<Long> longObservable) {
                        return longObservable.map(new Func1<Long, String>() {
                            @Override
                            public String call(Long aLong2) {
                                return aLong + " " + aLong2;
                            }
                        });
                    }
                })
                .subscribe(new Action1<Observable<String>>() {
                    @Override
                    public void call(Observable<String> stringObservable) {
                        stringObservable.subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.i(TAG, s);
                            }
                        });
                    }
                });
    }

    /**
     * 按照时间顺序，将两个或多个Observable合并成一个
     * <p>
     * 如果只有两个合并 可以使用o1.mergeWith(o2)
     * <p>
     * 当任何一个Observable出现onError时，则会终端后面所有的合并操作
     * <p>
     * 如果希望即便出现onError时，也要先合并完，最后再onError的话 ，可以使用mergeDelayError
     */
    @OnClick(R.id.btn_merge)
    void onMergeClick() {
        Observable<Long> o1 = Observable.interval(300, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(6);
        Observable<Long> o2 = Observable.interval(700, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong * 10).take(9);

        o1.mergeWith(o2).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError:" + e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(TAG, "onNext:" + aLong);
            }
        });
    }

    /**
     * 在序列的最前面增加指定项
     */
    @OnClick(R.id.btn_startWith)
    void onStartWithClick() {
        Observable.range(4, 6).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .startWith(1, 2, 3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, integer + "");

                    }
                });
    }

    /**
     * 在序列的最后面增加指定项
     */
    @OnClick(R.id.btn_concatWith)
    void onConcatWithClick() {

        Observable.range(4, 6).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .concatWith(Observable.range(0, 3))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, integer + "");

                    }
                });
    }

    /**
     * 在时间轴中，如果前一个没有执行完，而后一个开始执行了，那么将直接忽略掉前一个未执行的信息
     */
    @OnClick(R.id.btn_switchOnNext)
    void onSwitchOnNext() {
        Observable<Observable<Long>> o2 = Observable.interval(700, TimeUnit.MILLISECONDS).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(aLong -> aLong + 1).take(4)
                .map(new Func1<Long, Observable<Long>>() {
            @Override
            public Observable<Long> call(Long aLong) {
                return Observable.interval(300, TimeUnit.MILLISECONDS).take(4).map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong2) {
                        return aLong2 + aLong*10;
                    }
                });
            }
        });

        Observable.switchOnNext(o2).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.i(TAG, aLong + "");
            }
        });
    }

    /**
     * 按顺序结合o1 和 o2 的发射内容，返回的长度为发射的最少的长度，多余的直接忽略
     *
     * zipWith 只结合两个Observable
     *
     * 这里返回1 6 ， 2 7 ，3 8 （4被忽略了）
     */
    @OnClick(R.id.btn_zip)
    void onZipClick(){

        Observable<Integer> o1 = Observable.just(1, 2, 3, 4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());
        Observable<Integer> o2 = Observable.just(6, 7, 8).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());
        Observable
                .zip(o1, o2
                        , new Func2<Integer, Integer, String>() {
                            @Override
                            public String call(Integer integer, Integer integer2) {
                                return integer+" "+integer2;
                            }
                        })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG,s);
                    }
                });
    }



}
