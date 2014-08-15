package vn.siliconstraits.nicestop.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import vn.siliconstraits.nicestop.R;

public class MapsActivity extends FragmentActivity {
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String COUNT_KEY = "COUNT_KEY";

    // Data
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        // wearable
//        GoogleApiClient mGoogleAppiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle connectionHint) {
//                        Log.d(TAG, "onConnected: " + connectionHint);
//
//                        // Now you can use the data layer API
//                        PutDataMapRequest dataMap = PutDataMapRequest.create("/count");
//                        dataMap.getDataMap().putInt(COUNT_KEY, count++);
//                        PutDataRequest request = dataMap.asPutDataRequest();
//                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
//                                .putDataItem(mGoogleApiClient, request);
//                        peb
//
//                    }
//                    @Override
//                    public void onConnectionSuspended(int cause) {
//                        Log.d(TAG, "onConnectionSuspended: " + cause);
//                    }
//                })
//                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(ConnectionResult result) {
//                        Log.d(TAG, "onConnectionFailed: " + result);
//                    }
//                })
//
//                .addApi(Wearable.API)
//                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    // wearable

}
