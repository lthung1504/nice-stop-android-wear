package vn.siliconstraits.nicestop.activities;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.HashSet;

import vn.siliconstraits.nicestop.R;
import vn.siliconstraits.nicestop.base.BaseActivity;
import vn.siliconstraits.nicestop.data.Constant;
import vn.siliconstraits.nicestop.services.DataLayerMobileListenerService;
import vn.siliconstraits.nicestop.utils.LogManager;
import vn.siliconstraits.nicestop.utils.StandardAlertDialog;

/**
 * Created by HarmonyLee on 8/15/14.
 */
public class MainActivity extends BaseActivity {
    // Data
    private GoogleApiClient mGoogleApiClient;
    private DataApi.DataListener mListener;
    private Node mNode; // the connected device to send the message to

    // Get Location
    private boolean mIsGPSEnabled = false;
    private boolean mIsNetworkEnabled = false;
    private Location location;                                    // location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;        // meters
    private static final long MIN_TIME_BW_UPDATES = 3000;        // ms

    // Declaring a Location Manager
    protected LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check current location
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

//        if (!mIsGPSEnabled) {
//            showSettingsAlert();
//        }

        updateLocation(getLocation());
    }

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
                        mListener = new DataLayerMobileListenerService();
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
        getNodes();
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

        // location
        if (mLocationManager != null) mLocationManager.removeUpdates(locationListener);

        super.onStop();
    }

    private LocationListener locationListener	= new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            LogManager.logI(TAG, "onLocationChanged with location = " + location);
            updateLocation(location);
        }
    };

    public Location getLocation() {
        LogManager.logI(TAG, "getLocation");
        try {
            if (!mIsGPSEnabled && !mIsNetworkEnabled) {
                return null;
            } else {
                if (mIsGPSEnabled) {
                    LogManager.logD(TAG, "Use GPS to get location");
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (mIsNetworkEnabled) {
                    if (location == null) {
                        LogManager.logD(TAG, "Use Network to get location");
                        if (!mIsGPSEnabled) mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void updateLocation(Location location) {
        LogManager.logI(TAG, "updateLocation with location = " + location);

        // check can not get location
        if (location == null) {
            LogManager.logE(TAG, "Can not get your location. Please try later. Location is null");
//            mPbLoading.setVisibility(View.GONE);
            StandardAlertDialog.showWithDismissButton(getContext(), "Can not get your location. Please try later.");
            return;
        }

        // wrap location
        double longtitude = location.getLongitude();
        double lat = location.getLatitude();
        LogManager.logD(TAG, String.format("get location with location lat = %.5f long = %.5f", lat, longtitude));
        String locationPass = String.format("lat = %.5f long = %.5f", lat, longtitude);

        if (mNode != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(), "get location", locationPass.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    LogManager.logI(TAG, "onResult with sendMessageResult = " + sendMessageResult);
                }
            });
        }
        // give location to Android wear


//        LocationPitch6 locationPitch6 = new LocationPitch6();
//        locationPitch6.setLat(String.format("%.5f", lat).replace(",", "."));
//        locationPitch6.setLongitude(String.format("%.5f", longtitude).replace(",", "."));
//
//        // create list location from foursquare
//        NetworkHandler.getFoursquareLocations(locationPitch6, AddLocationActivity.this, new NetworkCallback() {
//
//            @Override
//            public void callback(Object object) {
//                LogManager.logI(TAG, String.format("callback with object = %s", object));
//
//                // active search
//                mFetSearchLocation.addTextChangedListener(textWatcherSearchLocation);
//
//                @SuppressWarnings("unchecked")
//                List<FoursquareLocation> items = (List<FoursquareLocation>) object;
//
//                // fill location to the list
//                mAdapter.setData(items);
//                mAdapter.notifyDataSetChanged();
//
//                mPbLoading.setVisibility(View.GONE);
//            }
//        }, new NetworkErrorCallback() {
//
//            @Override
//            public void callback(Object object) {
//                StandardAlertDialog.showErrorMessage(AddLocationActivity.this, object);
//                mPbLoading.setVisibility(View.GONE);
//            }
//        });
    }
    private void getNodes() {
        LogManager.logI(TAG, "getNodes");

        final HashSet<String> results = new HashSet<String>();
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    LogManager.logD(TAG, "node.getId() = " + node.getId());
                    results.add(node.getId());
                    mNode = node;
                }
            }
        });
    }

}
