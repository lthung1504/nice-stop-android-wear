package vn.siliconstraits.nicestop.activities;

import android.os.Bundle;
import android.widget.ListView;

import harmony.android.library.base.BaseActivity;
import vn.siliconstraits.nicestop.R;

/**
 * Created by HarmonyLee on 8/22/14.
 */
public class VenuesActivity extends BaseActivity {
    private ListView mLvVenues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get list venues
        //
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
}
