package harmony.android.library.base;

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
import java.net.URL;
import java.util.concurrent.TimeUnit;

import harmony.android.library.utils.LogManager;

public abstract class BaseDataLayerListenerService extends WearableListenerService implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
    protected static final String TAG = BaseDataLayerListenerService.class.getSimpleName();

    protected GoogleApiClient mGoogleApiClient = null;
    protected LocationClient mLocationClient = null;
    protected LocationRequest mLocationRequest = null;
    protected Node mPairNode = null;

    protected long lastPingTime = 0;

    // abstract method
    protected abstract void onMessageReceivedSuccess(MessageEvent m);

    protected void sendToPairDevice(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        LogManager.logI(TAG, String.format("sendToPairDevice with path = %s data = %s callback = %s", path, data, callback));
        if (mPairNode != null) {
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mPairNode.getId(), path, data);
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

    void findPairNodeAndBlock() {
        LogManager.logI(TAG, "findPairNodeAndBlock");
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if (result.getNodes().size() > 0) {
                    mPairNode = result.getNodes().get(0);
                    LogManager.logD(TAG, "Found wearable: name=" + mPairNode.getDisplayName() + ", id=" + mPairNode.getId());
                } else {
                    mPairNode = null;
                }
            }
        });
        int i = 0;
        while (mPairNode == null && i++ < 50) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // don't care
            }
        }
    }


    protected byte[] downloadUrl(URL toDownload) {
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
        if (checkOrSetUpGoogleApiClient())
            onMessageReceivedSuccess(m);
    }

    protected boolean checkOrSetUpGoogleApiClient() {
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
                return false;
            } else {

                LogManager.logD(TAG, "GoogleApiClient connect success, finding wearable node");
                findPairNodeAndBlock();
                LogManager.logD(TAG, "wearable node found");
            }
        } else if (mPairNode == null) {

            LogManager.logD(TAG, "GoogleApiClient was connceted but wearable not found, finding wearable node");
            findPairNodeAndBlock();
            if (mPairNode == null) {
                LogManager.logD(TAG, "wearable node not found");
                return false;
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

        return true;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LogManager.logI(TAG, "onDataChanged with dataEvents = " + dataEvents);
        // don't care
    }

    @Override
    public void onLocationChanged(Location location) {
        LogManager.logI(TAG, String.format("received location: lat = %f, long = %f, accuracy = %f", location.getLatitude(), location.getLongitude(), location.getAccuracy()));
        sendToPairDevice(String.format("location %f %f", location.getLatitude(), location.getLongitude()), null, null);

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
