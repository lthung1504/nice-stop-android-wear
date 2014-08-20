package vn.siliconstraits.nicestop;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import harmony.android.library.data.AppConfig;
import harmony.android.library.utils.LogManager;


public class MobileApplication extends Application {
    static final String TAG = MobileApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        LogManager.logD(TAG, "onCreate MyApplication");

        // Crashlytics
        if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.start(this);

    }

    private static MobileApplication instance;

    public MobileApplication() {
        instance = this;
    }

    public static MobileApplication getInstance() {
        return instance;
    }

    // public static void setApplication(Application a) {
    // instance = a;
    // }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

}
