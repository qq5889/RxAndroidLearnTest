package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

public class CombineActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_combine;
    }

    /**
     * 结合两条时间线上最近的数据（最近的左边和最近的右边）不重复
     */
    @OnClick(R.id.btn_combineLatest)
    void onCombineLatestClick() {
        Observable<Long> o1 = Observable.interval(300, TimeUnit.MILLISECONDS).take(20).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());
        Observable<Long> o2 = Observable.interval(200, TimeUnit.MILLISECONDS).take(30).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());

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
}
