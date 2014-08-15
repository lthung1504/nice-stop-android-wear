package vn.siliconstraits.nicestop.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import vn.siliconstraits.nicestop.R;
import vn.siliconstraits.nicestop.data.AppConfig;
import vn.siliconstraits.nicestop.utils.LogManager;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogManager.logI(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        if (AppConfig.IS_CRASHLYTICS_DEBUG) Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

    }
}
