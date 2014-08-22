package vn.siliconstraits.nicestop.network;

import harmony.android.library.data.AppConfig;
import harmony.android.library.utils.DateTimeUtil;

/**
 * Created by HarmonyLee on 8/20/14.
 */
public class Api {
    private static final String TAG = Api.class.getSimpleName();

    /*
         * foursquare location location: example: 40.7,-74
         */
    public static final String GET_FOURSQUARE_LOCATION(String lat, String lng) {
        return String.format("https://api.foursquare.com/v2/venues/search?ll=%s,%s&client_id=%s&client_secret=%s&radius=%d&v=%s", lat, lng, AppConfig.FOURSQUARE_CLIENT_ID, AppConfig.FOURSQUARE_CLIENT_SECRET, AppConfig.FOURSQUARE_RADIUS_SEARCH,
                DateTimeUtil.getCurrentDateWithFormat("yyyyMMdd"));
    }

}

