package vn.siliconstraits.nicestop.activities;

import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.wearable.MessageEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import harmony.android.library.base.BaseConnectionWearMobileActivity;
import harmony.android.library.data.Constant;
import harmony.android.library.model.VenueMobile;
import harmony.android.library.utils.LogManager;
import vn.siliconstraits.nicestop.R;
import vn.siliconstraits.nicestop.adapters.VenueAdapter;

/**
 * Created by HarmonyLee on 8/22/14.
 */
public class VenuesActivity extends BaseConnectionWearMobileActivity {
    private ListView mLvVenues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // send requeset get nearby venues
        double lat = 17.48609246;
        double lng = 106.60270214;
        sendToPairDevice(Constant.PATH_GET_NEARBY_VENUES, String.format("%f %f", lat, lng).getBytes(), null);
    }

    @Override
    protected void onMessageReceivedSuccess(MessageEvent m) {
        if (m.getPath().equals(Constant.PATH_GET_NEARBY_VENUES)) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(m.getData()));
                ArrayList<VenueMobile> list = (ArrayList<VenueMobile>) ois.readObject();
                mLvVenues.setAdapter(new VenueAdapter(getContext(), list));
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

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_venues;
    }

    @Override
    protected void setUpControl() {
        mLvVenues = (ListView) findViewById(R.id.listViewVenues);
    }

    @Override
    protected void setUpActionForControl() {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
