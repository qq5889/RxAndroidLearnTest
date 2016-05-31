package com.skyinno.rxandroidlearntest.activity;

import android.os.Bundle;
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

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MapRxActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_rx);
        ButterKnife.bind(this);
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
        Observable.just(10).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer s) {
                return String.valueOf(s);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String str) {
                Log.i(TAG, str);
            }
        });
    }


    @OnClick(R.id.btn_flatMap)
    public void onFlatMapClick() {
        List<Student> studentList = getStudentList();
        Observable.from(getStudentList()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
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
     * 与flatmap类似，但是concatMap是链式的，返回的顺序不会错乱
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
                    public void call(Student dis) {
                        Log.i(TAG, dis.name + " " + dis.disList.size());
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

}
