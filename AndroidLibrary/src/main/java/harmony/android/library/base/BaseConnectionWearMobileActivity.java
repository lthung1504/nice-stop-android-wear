package harmony.android.library.base;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import harmony.android.library.utils.LogManager;

/**
 * Created by HarmonyLee on 8/25/14.
 */
public abstract class BaseConnectionWearMobileActivity extends BaseActivity implements DataApi.DataListener, MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected GoogleApiClient mGoogleApiClient = null;
    protected Node mPairNode = null;

    // abstract method
    protected abstract void onMessageReceivedSuccess(MessageEvent m);
    protected abstract void onFinishSetupConnectionWearMobile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void sendToPairDevice(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        LogManager.logI(TAG, String.format("sendToPairDevice with path = %s data = %s callback = %s", path, data, callback));

        if (mPairNode != null) {
            LogManager.logD(TAG, "mPairNode.getDisplayName() = " + mPairNode.getDisplayName());
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mPairNode.getId(), path, data);
            pending.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    LogManager.logI(TAG, "onResult sendToPairDevice with result = " + result);
                    if (callback != null) {
                        callback.onResult(result);
                    }
                    if (!result.getStatus().isSuccess()) {
                        LogManager.logD(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                }
            });
        } else {
            LogManager.logE(TAG, "ERROR: tried to send message before device was found");
        }
    }

    void findPairNodeAndBlock() {
        LogManager.logI(TAG, "findPairNodeAndBlock");
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                LogManager.logI(TAG, "onResult findPairNodeAndBlock with result = " + result);
                if (result.getNodes().size() > 0) {
                    mPairNode = result.getNodes().get(0);
                    LogManager.logD(TAG, "Found wearable: name=" + mPairNode.getDisplayName() + ", id=" + mPairNode.getId());
                } else {
                    mPairNode = null;
                }
                onFinishSetupConnectionWearMobile();
            }
        });
        int i = 0;
        while (mPairNode == null && i++ < 50) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LogManager.logE(TAG, e);
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
    public void onDestroy() {
        LogManager.logI(TAG, "onDestroy");
        super.onDestroy();
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
        LogManager.logI(TAG, "checkOrSetUpGoogleApiClient");
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

        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LogManager.logI(TAG, "onConnected with bundle = " + bundle);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        findPairNodeAndBlock();
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

    @Override
    protected void onStart() {
        LogManager.logI(TAG, "onStart");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        LogManager.logI(TAG, "onStop");
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LogManager.logI(TAG, "onDataChanged with dataEvents = " + dataEvents);
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
            }
        }
    }
}
