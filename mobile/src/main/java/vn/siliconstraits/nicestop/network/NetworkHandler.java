package vn.siliconstraits.nicestop.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.List;

import harmony.android.library.utils.LogManager;
import vn.siliconstraits.nicestop.model.Venue;

public class NetworkHandler {
	public final static String	TAG	= NetworkHandler.class.getSimpleName();

    public static void getFoursquareLocations(String latitude, String longitude, Context context, final NetworkCallback callback, final NetworkErrorCallback errorCallback) {
        LogManager.logI(TAG, "getFoursquareLocations");
        String url = Api.GET_FOURSQUARE_LOCATION(latitude, longitude);

        LogManager.logD(TAG, String.format("call API with url = %s", url));
        Ion.with(context).load(url).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                LogManager.logI(TAG, String.format("onCompleted with e = %s result = %s", e, result));
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Venue>>() {
                }.getType();
                List<Venue> objects = gson.fromJson(result.toString(), listType);
                callback.callback(objects);
//                Object result;
//                if (result != null) {

//                    Type listType = new TypeToken<List<FoursquareLocation>>() {
//                    }.getType();
//                    List<FoursquareLocation> stores = gson.fromJson(object.optJSONObject("response").optString("venues"), listType);
//                    callback.callback(stores);
//                }
            }
        });
    }
}