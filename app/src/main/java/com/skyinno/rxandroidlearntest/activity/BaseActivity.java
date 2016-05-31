package com.skyinno.rxandroidlearntest.activity;

import android.support.v7.app.AppCompatActivity;

import com.skyinno.rxandroidlearntest.RxLifecycle;

/**
 * @author Jackie
 *         16/5/31 14:11:07
 */
public class BaseActivity extends AppCompatActivity implements RxLifecycle.Impl{
    private RxLifecycle mLifecycle = new RxLifecycle();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycle.onDestroy();
    }


    @Override
    public RxLifecycle bindLifecycle() {
        return mLifecycle;
    }
}
