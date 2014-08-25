package vn.siliconstraits.nicestop.services;

import com.google.android.gms.wearable.MessageEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import harmony.android.library.base.BaseDataLayerListenerService;
import harmony.android.library.data.Constant;
import harmony.android.library.model.Venue;
import harmony.android.library.utils.LogManager;


public class DataLayerWearListenerService extends BaseDataLayerListenerService {
    private static final String TAG = DataLayerWearListenerService.class.getSimpleName();

    public DataLayerWearListenerService(DataLayerWearListenerServiceListener listener) {
        super();
        mListener = listener;
    }

    public interface DataLayerWearListenerServiceListener {
        public void onGotNearbyVenues(List<Venue> venues);
    }

    private DataLayerWearListenerServiceListener mListener;

    @Override
    protected void onMessageReceivedSuccess(MessageEvent m) {
        LogManager.logI(TAG, "onMessageReceivedSuccess with m = " + m);

        // check if get path = get venues activity
        // show intent
        if (m.getPath().equals(Constant.PATH_GET_NEARBY_VENUES)) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(m.getData()));
                ArrayList<Venue> list = (ArrayList<Venue>) ois.readObject();
                if (mListener != null)
                    mListener.onGotNearbyVenues(list);
            } catch (IOException e) {
                LogManager.logE(TAG, e);
            } catch (ClassNotFoundException e) {
                LogManager.logE(TAG, e);
            } finally {
                if (ois != null)
                    try {
                        ois.close();
                    } catch (IOException e) {
                        LogManager.logE(TAG, e);
                    }
            }


        }

    }

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        LogManager.logI(TAG, "onDataChanged with dataEvents = " + dataEvents);
//
//        if (Log.isLoggable(TAG, Log.DEBUG)) {
//            Log.d(TAG, "onDataChanged: " + dataEvents);
//        }
//        final List<DataEvent> events = FreezableUtils
//                .freezeIterable(dataEvents);
//
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();
//
//        ConnectionResult connectionResult =
//                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
//
//        if (!connectionResult.isSuccess()) {
//            Log.e(TAG, "Failed to connect to GoogleApiClient.");
//            return;
//        }
//
//        // Loop through the events and send a message
//        // to the node that created the data item.
//        for (DataEvent event : events) {
//            Uri uri = event.getDataItem().getUri();
//
//            // Get the node id from the host value of the URI
//            String nodeId = uri.getHost();
//            // Set the data of the message to be the bytes of the URI.
//            byte[] payload = uri.toString().getBytes();
//
//            // Send the RPC
//            Wearable.MessageApi.sendMessage(googleApiClient, nodeId,
//                    DATA_ITEM_RECEIVED_PATH, payload);
//        }
//    }
//
//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//        LogManager.logI(TAG, "onMessageReceived with messageEvent = " + messageEvent);
//        super.onMessageReceived(messageEvent);
//    }
}
