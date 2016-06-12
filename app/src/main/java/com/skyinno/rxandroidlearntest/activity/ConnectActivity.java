package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

/**
 * 一知半解，需要深入理解
 */
public class ConnectActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_connect;
    }

    /**
     * 可连接的Observable (connectable Observable)与普通的Observable差不多，不过它并不会在被订阅时开始发射数据，而是直到使用了Connect操作符时才会开始。用这种方法，你可以在任何时候让一个Observable开始发射数据。
     * <p>
     * RxJava中connect是ConnectableObservable接口的一个方法
     * 使用publish操作符可以将一个普通的Observable转换为一个ConnectableObservable。
     * <p>
     * 调用ConnectableObservable的connect方法会让它后面的Observable开始给发射数据给订阅者。
     * <p>
     * connect方法返回一个Subscription对象，可以调用它的unsubscribe方法让Observable停止发射数据给观察者。
     * <p>
     * 即使没有任何订阅者订阅它，你也可以使用connect方法让一个Observable开始发射数据（或者开始生成待发射的数据）。这样，你可以将一个"冷"的Observable变为"热"的。
     */
    @OnClick(R.id.btn_connect)
    void onConnectClick() {
        ConnectableObservable<Integer> o1 = Observable.range(0, 1).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .publish(); //将普通的Observable转换为可连接的Observable
        o1.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, integer + "_1");
            }
        });
        // 开始执行
        o1.connect();
    }

    /**
     * share()等价于 .publish().refCount()
     * <p>
     * RefCount操作符把从一个可连接的Observable连接和断开的过程自动化了。
     * 它操作一个可连接的Observable，返回一个普通的Observable。
     * 当第一个订阅者订阅这个Observable时，RefCount连接到下层的可连接Observable。RefCount跟踪有多少个观察者订阅它，直到最后一个观察者完成才断开与下层可连接Observable的连接。
     */
    @OnClick(R.id.btn_share)
    void onShareClick() {
        Observable<Integer> o1 = Observable.range(0, 1).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .share(); //将普通的Observable转换为可连接的Observable再转成看起来像普通的Observable

        o1.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, integer + "_1");
            }
        });
    }


    /**
     * 如果在将一个Observable转换为可连接的Observable之前对它使用Replay操作符，
     * 产生的这个可连接Observable将总是发射完整的数据序列给任何未来的观察者，
     * 即使那些观察者在这个Observable开始给其它观察者发射数据之后才订阅。
     */

}
