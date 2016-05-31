package com.skyinno.rxandroidlearntest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * @author Jackie
 *         16/2/16 10:52:27
 */
public class App extends Application {
    private static Context _context;

    @Override
    public void onCreate() {
        super.onCreate();
        _context = this;

    }

    public static Context getAppContext() {
        return _context;
    }


    /**
     * toast
     */
    public static void toast(@NonNull CharSequence text) {
        Toast.makeText(_context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * toast
     */
    public static void toast(@StringRes int stringRes) {
        Toast.makeText(_context, stringRes, Toast.LENGTH_SHORT).show();
    }
}
