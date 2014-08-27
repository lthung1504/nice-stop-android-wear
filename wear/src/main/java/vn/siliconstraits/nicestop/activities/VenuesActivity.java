package vn.siliconstraits.nicestop.activities;

import android.widget.ListView;

import com.google.android.gms.wearable.MessageEvent;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

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
    private VenueAdapter mVenueAdapter;

    @Override
    protected void onFinishSetupConnectionWearMobile() {
        // send requeset get nearby venues
        double lat = 17.48609246;
        double lng = 106.60270214;
        sendToPairDevice(Constant.PATH_GET_NEARBY_VENUES, String.format("%f %f", lat, lng).getBytes(), null);
    }

    @Override
    protected void onMessageReceivedSuccess(MessageEvent m) {
        LogManager.logI(TAG, "onMessageReceivedSuccess with m = " + m);
        if (m.getPath().equals(Constant.PATH_GET_NEARBY_VENUES)) {
            List<VenueMobile> venueMobiles = new ArrayList<VenueMobile>();
            // read from byte array
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(m.getData());
                ObjectInputStream in = new ObjectInputStream(bais);

                try {
                    while (true) {
                        VenueMobile venueMobile = (VenueMobile) in.readObject();
                        venueMobiles.add(venueMobile);
                    }
                } catch (EOFException e) {
                    // don't worry, it's the end of byte array
                }

                LogManager.logD(TAG, "venueMobiles.size() = " + venueMobiles.size());

                mVenueAdapter = new VenueAdapter(getContext(), venueMobiles);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogManager.logD(TAG, "run on UI thread");
                        mLvVenues.setAdapter(mVenueAdapter);
                        mVenueAdapter.notifyDataSetChanged();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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
