package vn.siliconstraits.nicestop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.HashSet;

import harmony.android.library.base.BaseActivity;
import harmony.android.library.utils.LogManager;
import vn.siliconstraits.nicestop.R;

import static com.google.android.gms.wearable.MessageApi.SendMessageResult;
import static com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;

public class MainActivity extends BaseActivity {
    private static final String STORE_CURRENT_LOCATION_KEY = "STORE_CURRENT_LOCATION_KEY";

    // Control
    private TextView mTextView;
    private Button mBtnSayNiceStop;

    // Data
    private Node mNode; // the connected device to send the message to
    private GoogleApiClient mGoogleApiClient;
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setUpControl() {
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });
        mBtnSayNiceStop = (Button) findViewById(R.id.buttonSayNiceStop);
    }

    @Override
    protected void setUpActionForControl() {
        mBtnSayNiceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogManager.logV(TAG, "onClick mBtnSayNiceStop");

                // TODO: send message store current location
//                PutDataMapRequest dataMap = PutDataMapRequest.create("/count");
//                dataMap.getDataMap().putBoolean(STORE_CURRENT_LOCATION_KEY, true);
//                PutDataRequest request = dataMap.asPutDataRequest();
//                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
//                        .putDataItem(mGoogleApiClient, request);
//
//                pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
//                    @Override
//                    public void onResult(DataApi.DataItemResult dataItemResult) {
//                        LogManager.logI(TAG, "onResult with dataItemResult = " + dataItemResult);
//                        Toast.makeText(getContext(), "onResult with dataItemResult = " + dataItemResult, Toast.LENGTH_LONG).show();
//
//                        // temp
//                        if(dataItemResult.getStatus().isSuccess()) {
//                            Log.d(TAG, "Data item set: " + dataItemResult.getDataItem().getUri());
//                        }
//                    }
//                });

                // send message
                if (mNode != null) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, mNode.getId(), START_ACTIVITY_PATH, "data byte array".getBytes()).setResultCallback(new ResultCallback<SendMessageResult>() {
                        @Override
                        public void onResult(SendMessageResult sendMessageResult) {
                            LogManager.logI(TAG, "onResult with sendMessageResult = " + sendMessageResult);
//                        if (!result.getStatus().isSuccess()) {
//                            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
//                        }
                        }
                    });
                }


            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
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

    private void getNodes() {
        LogManager.logI(TAG, "getNodes");

        final HashSet<String> results = new HashSet<String>();
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<GetConnectedNodesResult>() {
            @Override
            public void onResult(GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    LogManager.logD(TAG, "node.getId() = " + node.getId());
                    results.add(node.getId());
                    mNode = node;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        LogManager.logI(TAG, "onStart");
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
