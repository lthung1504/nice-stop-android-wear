package vn.siliconstraits.nicestop.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Wearable;

import vn.siliconstraits.nicestop.R;
import vn.siliconstraits.nicestop.base.BaseActivity;
import vn.siliconstraits.nicestop.services.DataLayerListenerService;
import vn.siliconstraits.nicestop.utils.LogManager;

/**
 * Created by HarmonyLee on 8/15/14.
 */
public class MainActivity extends BaseActivity {
    private GoogleApiClient mGoogleApiClient;
    DataApi.DataListener mListener;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setUpControl() {

    }

    @Override
    protected void setUpActionForControl() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
                        mListener = new DataLayerListenerService();
                        Wearable.DataApi.addListener(mGoogleApiClient, mListener);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onStart() {
        LogManager.logI(TAG, "onStart");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, mListener);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
}
