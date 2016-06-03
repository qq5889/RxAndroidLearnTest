package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

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
     *
     * 在Observable发生错误或者异常的时候，拦截错误并执行指定逻辑，返回一个与源数据相同类型的结果，并终止后续发射。最终回调订阅者的omCompleted
     *
     */
    @OnClick(R.id.btn_onErrorReturn)
    void onErrorReturnClick(){
        Observable.range(3,9).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        if(integer == 5){
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
                Log.i(TAG,"onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"onError:"+e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG,"onNext:"+integer);
            }
        });
    }
}
