package vn.siliconstraits.nicestop.activities;

import android.content.Intent;

import harmony.android.library.base.BaseActivity;
import vn.siliconstraits.nicestop.R;
import vn.siliconstraits.nicestop.services.DataLayerMobileListenerService;

/**
 * Created by HarmonyLee on 8/18/14.
 */
public class HomeActivity extends BaseActivity {

    private Intent intent;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setUpControl() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        // start service
        intent = new Intent(this, DataLayerMobileListenerService.class);
        startService(intent);
    }

    @Override
    protected void onStop() {
        stopService(intent);
        super.onStop();
    }

    @Override
    protected void setUpActionForControl() {

    }
}
