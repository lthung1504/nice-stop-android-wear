package harmony.android.library.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;

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

    public void goToActivity(Class objectClass, Bundle bundle) {
        goToActivity(objectClass, false, bundle);
    }

    public void goToActivity(Class objectClass) {
        goToActivity(objectClass, false, null);
    }

    public void goToActivity(Class objectClass, boolean isClearHistoryActivity, Bundle bundle) {
        goToActivity(objectClass, isClearHistoryActivity, false, bundle);
    }

    public void goToActivity(Class objectClass, boolean isClearHistoryActivity) {
        goToActivity(objectClass, isClearHistoryActivity, false, null);
    }

    public void goToActivity(Class objectClass, boolean isClearHistoryActivity, boolean isNoAnimation, Bundle bundle) {
        LogManager.logI(TAG, "goToActivity " + objectClass);

        Intent i = createIntent(objectClass, isClearHistoryActivity, isNoAnimation, bundle);

        //		if (isClearHistoryActivity)
        //			startActivity(i);
        //		else
        //			startActivityForResult(i, ACTIVITY_RESULT_CODE_ANIMATION);
        startActivity(i);
    }

    private Intent createIntent(Class objectClass, boolean isClearHistoryActivity, boolean isNoAnimation, Bundle bundle) {
        Intent i = new Intent(this, objectClass);
        if (isClearHistoryActivity) {
            LogManager.logD(TAG, "go to home activity set clear all task");
            i.setFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (isNoAnimation) i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (bundle != null) i.putExtras(bundle);
        return i;
    }

}
