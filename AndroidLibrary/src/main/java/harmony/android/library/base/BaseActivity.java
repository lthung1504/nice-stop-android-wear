package harmony.android.library.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import harmony.android.library.utils.LogManager;


public abstract class BaseActivity extends Activity {
    protected static String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Debug
        TAG = getClass().getSimpleName();
        LogManager.logI(TAG, "onCreate");

        // Get bundle data
        LogManager.logI(TAG, "setUpDataInputFromBundle");
        setUpDataInputFromBundle(getIntent().getExtras());

        // Layout
        LogManager.logI(TAG, "getLayoutResourceId");
        setContentView(getLayoutResourceId());

        // Bind control to layout
        LogManager.logI(TAG, "setUpControl");
        setUpControl();

        // Hook action to control
        LogManager.logI(TAG, "setUpActionForControl");
        setUpActionForControl();

        LogManager.logI(TAG, "finish oncreate base");
    }


    protected abstract int getLayoutResourceId();

    protected abstract void setUpControl();

    protected abstract void setUpActionForControl();

    protected void setUpDataInputFromBundle(Bundle bundle) {
    }

    public Context getContext() {
        return this;
    }
}
