package vn.siliconstraits.nicestop.base;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import vn.siliconstraits.nicestop.data.AppConfig;
import vn.siliconstraits.nicestop.utils.LogManager;


public class MyApplication extends Application {
    static final String TAG = MyApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        LogManager.logD(TAG, "onCreate MyApplication");

        // Crashlytics
        if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.start(this);

    }

    private static MyApplication instance;

    public MyApplication() {
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    // public static void setApplication(Application a) {
    // instance = a;
    // }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

}
