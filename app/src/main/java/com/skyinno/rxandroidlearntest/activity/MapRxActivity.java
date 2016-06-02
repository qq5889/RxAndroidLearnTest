package com.skyinno.rxandroidlearntest.activity;

import android.os.Looper;
import android.util.Log;

import com.skyinno.rxandroidlearntest.R;
import com.skyinno.rxandroidlearntest.RxLifecycle;
import com.skyinno.rxandroidlearntest.RxThreadUtils;
import com.skyinno.rxandroidlearntest.bean.Dis;
import com.skyinno.rxandroidlearntest.bean.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

public class MapRxActivity extends BaseActivity {


    @Override
    protected int layoutRes() {
        return R.layout.activity_map_rx;
    }

    private void checkMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.i(TAG, "______________mainThread");
        } else {
            Log.i(TAG, "not____________mainThread");
        }
    }

    @OnClick(R.id.btn_map1)
    public void onMapClick() {
        Observable.just(10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert()).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer s) {
                return String.valueOf(s);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String str) {
                Log.i(TAG, str);
            }
        });
    }

    /**
     * 将原始发射的数据强转为一个指定类型，是map的特殊版本
     */
    @OnClick(R.id.btn_cast)
    void onCastClick() {
        Observable.just(10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .cast(String.class).subscribe(new Action1<String>() {
            @Override
            public void call(String str) {
                Log.i(TAG, str);
            }
        });
    }


    /**
     * 变换，内容耗时时，不会等待其执行完才会进行下一个。
     */
    @OnClick(R.id.btn_flatMap)
    public void onFlatMapClick() {
        List<Student> studentList = getStudentList();
        Observable.from(getStudentList()).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .flatMap(new Func1<Student, Observable<Dis>>() {//先全部执行并持有后，才会走subscribe
                    @Override
                    public Observable<Dis> call(Student student) {
                        checkMainThread();
                        return Observable.from(student.disList).delay(student.disList.size() < 5 ? 1 : 0, TimeUnit.SECONDS);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Dis>() {
                    @Override
                    public void call(Dis dis) {
                        checkMainThread();
                        Log.i(TAG, dis.name + " " + dis.grade);
                    }
                });
    }

    /**
     * 与flatmap类似，但是concatMap是链式的，前一个执行完后才会执行后一个，返回的顺序不会错乱
     */
    @OnClick(R.id.btn_concatMap)
    void onConcatMapClick() {
        Observable.from(getStudentList()).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .concatMap(new Func1<Student, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(Student student) {
                        return Observable.just(student).delay(student.disList.size() < 5 ? 1 : 0, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        Log.i(TAG, student.name + " " + student.disList.size());
                    }
                });
    }

    /**
     * 与flatmap类似，但如果某一个进行耗时时，按顺序下一个如果已经开始执行，那么，会舍弃前面的内容
     */
    @OnClick(R.id.btn_switchMap)
    void onSwitchMapClick() {
        Observable.from(getStudentList()).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .switchMap(new Func1<Student, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(Student student) {
                        return Observable.just(student).delay(student.disList.size() < 2 ? 1 : 0, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        Log.i(TAG, student.name + " " + student.disList.size());
                    }
                });
    }


    private List<Student> getStudentList() {
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<Dis> disList = new ArrayList<>();
            list.add(new Student("name_" + i, disList));
            for (int j = 0; j < i + 1; j++) {
                disList.add(new Dis("project_" + j, 90 + j));
            }
        }
        return list;
    }

    /**
     * 将数据缓存起来，每隔（n）长时间，将在这段时间中接受到的缓存数据发射给观察者
     */
    @OnClick(R.id.btn_buffer)
    void onBufferClick() {
        Observable.interval(1, TimeUnit.SECONDS)
                .limit(10)
                .compose(RxLifecycle.bindLifecycle(this))
                .compose(RxThreadUtils.convert())
                .buffer(3, TimeUnit.SECONDS)
                .flatMap(new Func1<List<Long>, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(List<Long> longs) {
                        return Observable.from(longs);
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long longs) {
                        Log.i(TAG, longs + "");
                    }
                });
    }

    /**
     * 分组操作，将内容按照一定的规则分组，返回一个GroupedObservable
     */
    @OnClick(R.id.btn_groupBy)
    void onGroupByClick() {
        Observable.interval(1, TimeUnit.SECONDS).take(10)
                .compose(RxLifecycle.bindLifecycle(this))
                .compose(RxThreadUtils.convert())
                .groupBy(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return aLong % 3; // 分三组 key 分别为 0,1,2
                    }
                }).subscribe(new Action1<GroupedObservable<Long, Long>>() {
            @Override
            public void call(GroupedObservable<Long, Long> longLongGroupedObservable) {
                longLongGroupedObservable.subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        long key = longLongGroupedObservable.getKey();
                        Log.i(TAG, "key:" + key + " value:" + aLong);
                    }
                });
            }
        });
    }


    /**
     * 操作符对原始Observable发射的第一项数据应用一个函数，然后将那个函数的结果作为自己的第一项数据发射。
     * 它将函数的结果同第二项数据一起填充给这个函数来产生它自己的第二项数据。
     * 它持续进行这个过程来产生剩余的数据序列。
     * 结果 0 1 3 6 10 15 21 28 ...
     */
    @OnClick(R.id.btn_scan)
    void onScanClick() {
        Observable.interval(1000, TimeUnit.MILLISECONDS).take(10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .scan(new Func2<Long, Long, Long>() {
                    @Override
                    public Long call(Long aLong, Long aLong2) {
                        return aLong2 + aLong;
                    }
                }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                Log.i(TAG, aLong + "");
            }
        });
    }

    /**
     * 同buffer很相似，不过buffer是将数据存到缓存列表，而window是存入一个Observables
     */
    @OnClick(R.id.btn_window)
    void onWindowClick(){
        Observable.interval(1,TimeUnit.SECONDS).take(10).compose(RxLifecycle.bindLifecycle(this)).compose(RxThreadUtils.convert())
                .window(3,TimeUnit.SECONDS)
                .subscribe(new Action1<Observable<Long>>() {
                    @Override
                    public void call(Observable<Long> longObservable) {
                        Log.i(TAG,"startLongObservable");
                        longObservable.subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                Log.i(TAG,aLong+"");
                            }
                        });
                    }
                });
    }

}
