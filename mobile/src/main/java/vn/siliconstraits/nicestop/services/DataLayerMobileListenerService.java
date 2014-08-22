package vn.siliconstraits.nicestop.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import harmony.android.library.data.Constant;
import harmony.android.library.model.Venue;
import harmony.android.library.utils.LogManager;
import vn.siliconstraits.nicestop.network.NetworkCallback;
import vn.siliconstraits.nicestop.network.NetworkErrorCallback;
import vn.siliconstraits.nicestop.network.NetworkHandler;

public class DataLayerMobileListenerService extends WearableListenerService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = DataLayerMobileListenerService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient = null;
    private LocationClient mLocationClient = null;
    private LocationRequest mLocationRequest = null;
    private Node mWearableNode = null;

    private long lastPingTime = 0;

    private void sendToWearable(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        LogManager.logI(TAG, String.format("sendToWearable with path = %s data = %s callback = %s", path, data, callback));
        if (mWearableNode != null) {
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), path, data);
            pending.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    LogManager.logI(TAG, "onResult with result = " + result);
                    if (callback != null) {
                        callback.onResult(result);
                    }
                    if (!result.getStatus().isSuccess()) {
                        LogManager.logD(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                }
            });
        } else {
            LogManager.logD(TAG, "ERROR: tried to send message before device was found");
        }
    }

    void findWearableNodeAndBlock() {
        LogManager.logI(TAG, "findWearableNodeAndBlock");
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if (result.getNodes().size() > 0) {
                    mWearableNode = result.getNodes().get(0);
                    LogManager.logD(TAG, "Found wearable: name=" + mWearableNode.getDisplayName() + ", id=" + mWearableNode.getId());
                } else {
                    mWearableNode = null;
                }
            }
        });
        int i = 0;
        while (mWearableNode == null && i++ < 50) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // don't care
            }
        }
    }

    private void onMessageLocate() {
        LogManager.logI(TAG, "onMessageLocate");

        if (mLocationClient != null && mLocationClient.isConnected()) {
            Location location = mLocationClient.getLastLocation();
            if (location == null) {
                LogManager.logD(TAG, "No location available");
            } else {

                LogManager.logD(TAG, String.format("Got location: %f %f %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
                sendToWearable(String.format("location %f %f", location.getLatitude(), location.getLongitude()), null, null);
            }
        }
    }

    private void onMessageGetNearbyVenues(String lat, String lng) {
        LogManager.logI(TAG, "onMessageGetNearbyVenues");
        NetworkHandler.getFoursquareLocations(lat, lng, this, new NetworkCallback() {
            @Override
            public void callback(Object object) {
                // send to wearable
                try {
                    List<Venue> objects = (List<Venue>) object;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(baos);
                    for (Venue element : objects) {
                        out.writeObject(element);
                    }
                    byte[] bytes = baos.toByteArray();
                    sendToWearable(Constant.PATH_GET_NEARBY_VENUES, bytes, null);
                } catch (IOException e) {
                    LogManager.logE(TAG, e);
                }
            }
        }, new NetworkErrorCallback() {
            @Override
            public void callback(Object object) {
                LogManager.logE(TAG, "can not call network");
            }
        });

}

    private void onMessageGet(final int y, final int x, final double latitude, final double longitude, final int googleZoom) {
        LogManager.logI(TAG, String.format("onMessageGet(lat = %f, long = %f, googleZoom = %d)", latitude, longitude, googleZoom));

        final String maptype = "roadmap";
        final String format = "jpg";

        InputStream is;
        final String url = String.format(
                "http://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=%d&size=256x282&maptype=%s&format=%s",
                latitude, longitude, googleZoom, maptype, format);

        LogManager.logD(TAG, "onMessageGet: url: " + url);

        new Thread(new Runnable() {
            public void run() {
                try {
                    // download the image and send it to the wearable
                    try {
                        byte[] outdata = downloadUrl(new URL(url));
                        LogManager.logD(TAG, String.format("read %d bytes", outdata.length));
                        sendToWearable(String.format("response %d %d %f %f %d", y, x, latitude, longitude, googleZoom), outdata, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    LogManager.logE(TAG, "onMessageGet: exception:", e);
                }
            }
        }).start();
    }

    private byte[] downloadUrl(URL toDownload) {
        LogManager.logI(TAG, "downloadUrl with toDownload = " + toDownload);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[16384];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return outputStream.toByteArray();
    }

    @Override
    public void onCreate() {
        LogManager.logI(TAG, "onCreate");
        super.onCreate();

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            LogManager.logE(TAG, e);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogManager.logI(TAG, String.format("onStartCommand with intent = %s flags = %d startId = %d", intent, flags, startId));
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogManager.logI(TAG, "onDestroy");
        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
        super.onDestroy();
    }

    @Override
    public void onPeerConnected(Node peer) {
        LogManager.logI(TAG, "onPeerConnected with peer = " + peer);
        super.onPeerConnected(peer);
        LogManager.logD(TAG, "Connected: name=" + peer.getDisplayName() + ", id=" + peer.getId());
    }

    // ************************* manage message
    @Override
    public void onMessageReceived(MessageEvent m) {
        String dataString = new String(m.getData());
        LogManager.logI(TAG, String.format("onMessageReceived with m.getPath = %s m.getData = %s dataString = %s", m.getPath(), m.getData(), dataString));

        checkOrSetUpGoogleApiClient();
        String requestType = m.getPath();
        Scanner scanner = new Scanner(dataString);

        if (requestType.equals(Constant.PATH_GET_LOCATION_MAP)) {
            // take x
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            double latitude = scanner.nextDouble();
            double longitude = scanner.nextDouble();
            // take googleZoom
            int googleZoom = scanner.nextInt();
            onMessageGet(y, x, latitude, longitude, googleZoom);
        } else if (requestType.equals(Constant.PATH_GET_NEARBY_VENUES)) {
            String lat = scanner.next();
            String lng = scanner.next();
            onMessageGetNearbyVenues(lat, lng);
        }
//        else if (requestType.equals()) {
//            onMessageLocate();
//        }
    }

    private void checkOrSetUpGoogleApiClient() {
        if (mGoogleApiClient == null) {
            LogManager.logD(TAG, "setting up GoogleApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addApi(LocationServices.API)
                    .build();
            LogManager.logD(TAG, "connecting to GoogleApiClient");
            ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

            if (!connectionResult.isSuccess()) {
                LogManager.logE(TAG, String.format("GoogleApiClient connect failed with error code %d", connectionResult.getErrorCode()));
//                return;
            } else {

                LogManager.logD(TAG, "GoogleApiClient connect success, finding wearable node");
                findWearableNodeAndBlock();
                LogManager.logD(TAG, "wearable node found");
            }
        } else if (mWearableNode == null) {

            LogManager.logD(TAG, "GoogleApiClient was connceted but wearable not found, finding wearable node");
            findWearableNodeAndBlock();
            if (mWearableNode == null) {
                LogManager.logD(TAG, "wearable node not found");
//                return;
            }
        }

        if (mLocationClient == null) {
            if (mLocationClient == null) mLocationClient = new LocationClient(this, this, this);
        }

        if (!mLocationClient.isConnected()) {
            mLocationClient.connect();
            while (mLocationClient.isConnecting()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LogManager.logI(TAG, "onDataChanged with dataEvents = " + dataEvents);
        // don't care
    }

    @Override
    public void onLocationChanged(Location location) {

        LogManager.logI(TAG, String.format("received location: lat = %f, long = %f, accuracy = %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
        sendToWearable(String.format("location %f %f", location.getLatitude(), location.getLongitude()), null, null);

        if (System.currentTimeMillis() - lastPingTime > 15000) {
            LogManager.logD(TAG, String.format("ping timeout %d ms, disconnecting", System.currentTimeMillis() - lastPingTime));
            mLocationClient.removeLocationUpdates(this);
            mLocationClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LogManager.logI(TAG, "onConnected with bundle = " + bundle);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() {
        LogManager.logI(TAG, "onDisconnected");
        mLocationClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LogManager.logI(TAG, "connection failed");
        if (connectionResult.hasResolution()) {
            LogManager.logD(TAG, "has resolution");
        } else {
            LogManager.logD(TAG, "no resolution");
        }
    }
}
