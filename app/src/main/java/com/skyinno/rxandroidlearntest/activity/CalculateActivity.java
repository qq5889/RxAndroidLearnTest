package com.skyinno.rxandroidlearntest.activity;

import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;

import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

public class CalculateActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_calculate;
    }

    /**
     * 求和
     */
    @OnClick(R.id.btn_count)
    void onCountClick() {
        Observable.range(2, 4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .count()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "合计：" + integer);
                    }
                });
    }

    /**
     * 拼接操作
     * <p>
     * Concat操作符连接多个Observable的输出，就好像它们是一个Observable，第一个Observable发射的所有数据在第二个Observable发射的任何数据前面，以此类推。
     * <p>
     * 直到前面一个Observable终止，Concat才会订阅额外的一个Observable。注意：因此，如果你尝试连接一个"热"Observable（这种Observable在创建后立即开始发射数据，即使没有订阅者），Concat将不会看到也不会发射它之前发射的任何数据。
     * <p>
     * 在ReactiveX的某些实现中有一种ConcatMap操作符（名字可能叫concat_all, concat_map, concatMapObserver, for, forIn/for_in, mapcat,
     * selectConcat或selectConcatObserver），他会变换原始Observable发射的数据到一个对应的Observable，然后再按观察和变换的顺序进行连接操作。
     * <p>
     * StartWith操作符类似于Concat，但是它是插入到前面，而不是追加那些Observable的数据到原始Observable发射的数据序列。
     * <p>
     * Merge操作符也差不多，它结合两个或多个Observable的发射物，但是数据可能交错，而Concat不会让多个Observable的发射物交错。
     */
    @OnClick(R.id.btn_concat)
    void onConcatClick() {
        Observable<Integer> o1 = Observable.range(10, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert());
        Observable.range(1, 4).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .concatWith(o1)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "" + integer);
                    }
                });
    }

    /**
     * 累积操作
     * <p>
     * Reduce操作符对原始Observable发射数据的第一项应用一个函数，然后再将这个函数的返回值与第二项数据一起传递给函数，以此类推，持续这个过程知道原始Observable发射它的最后一项数据并终止，此时Reduce返回的Observable发射这个函数返回的最终值。
     * <p>
     * 在其它场景中，这种操作有时被称为累积，聚集，压缩，折叠，注射等。
     * <p>
     * <p>
     * 提示：不建议使用reduce收集发射的数据到一个可变的数据结构，那种场景你应该使用collect。
     * <p>
     * collect
     * <p>
     * collect与reduce类似，但它的目的是收集原始Observable发射的所有数据到一个可变的数据结构，collect生成的这个Observable会发射这项数据。它需要两个参数：
     * <p>
     * 一个函数返回可变数据结构
     * 另一个函数，当传递给它这个数据结构和原始Observable发射的数据项时，适当地修改数据结构。
     */
    @OnClick(R.id.btn_reduce)
    void onReduceClick() {
        Observable.range(1, 5).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.i(TAG, "累积量为：" + integer);
                    }
                });
    }

}
