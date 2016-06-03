package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class CatchActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_catch;
    }

    /**
     * 在Observable发生错误或者异常的时候，拦截错误并执行指定逻辑，返回一个与源数据相同类型的结果，并终止后续发射。最终回调订阅者的omCompleted
     */
    @OnClick(R.id.btn_onErrorReturn)
    void onErrorReturnClick() {
        Observable.range(3, 9).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if (integer == 5) {
                            throw new RuntimeException("error");
                        }
                        return integer;
                    }
                }).onErrorReturn(new Func1<Throwable, Integer>() {
            @Override
            public Integer call(Throwable throwable) {

                return 10000;
            }
        }).subscribe(new Subscriber<Integer>() {
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
     * 同onErrorResult类似，只不过当Observable在遇到错误时开始发射第二个Observable的数据序列。
     */
    @OnClick(R.id.btn_onErrorResumeNext)
    void onErrorResumeNext() {
        Observable.range(3, 9).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if (integer == 5) {
                            throw new RuntimeException("error");
                        }
                        return integer;
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Integer>>() {
                    @Override
                    public Observable<? extends Integer> call(Throwable throwable) {
                        return Observable.range(20, 5);
                    }
                })
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

    /**
     * 效果等同onErrorResumeNext , 只不过不做异常处理
     */
    @OnClick(R.id.btn_onExceptionErrorResumeNext)
    void onExceptionErrorResumeNext(){

        Observable.range(3, 9).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if (integer == 5) {
                            throw new RuntimeException("error");
                        }
                        return integer;
                    }
                })
                .onExceptionResumeNext(Observable.range(20,3))
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

    /**
     * retry操作符是当Observable发生错误或者异常的时候，重新尝试执行Observable，如果经过n次重新尝试执行后仍然出现错误或者异常，则最后回调执行onError方法
     */
    @OnClick(R.id.btn_retry)
    void onRetryClick(){
        Observable.range(3, 9).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if (integer == 5) {
                            throw new RuntimeException("error哈哈哈");
                        }
                        return integer;
                    }
                })
                .retry(2)
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


    /**
     * 每次出现错误或者异常的时候，会进入fun1，执行一些个人操作，比如一秒后重新连接网络，直到连上为止。
     */
    @OnClick(R.id.btn_retryWhen)
    void onRetryWhenClick(){
        Observable.range(3, 9).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if (integer == 5) {
                            throw new RuntimeException("error");
                        }
                        return integer;
                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable.delay(1, TimeUnit.SECONDS);
                    }
                })
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
