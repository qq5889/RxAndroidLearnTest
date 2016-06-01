package com.skyinno.rxandroidlearntest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.skyinno.rxandroidlearntest.RxLifecycle;

import butterknife.ButterKnife;

/**
 * @author Jackie
 *         16/5/31 14:11:07
 */
public abstract class BaseActivity extends AppCompatActivity implements RxLifecycle.Impl{
    private RxLifecycle mLifecycle = new RxLifecycle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());
        ButterKnife.bind(this);
    }

    protected abstract int layoutRes();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycle.onDestroy();
        ButterKnife.unbind(this);
    }


    @Override
    public RxLifecycle bindLifecycle() {
        return mLifecycle;
    }
}
