package vn.siliconstraits.nicestop.services;

import com.google.android.gms.wearable.MessageEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import harmony.android.library.base.BaseDataLayerListenerService;
import harmony.android.library.data.Constant;
import harmony.android.library.model.VenueMobile;
import harmony.android.library.utils.LogManager;
import vn.siliconstraits.nicestop.model.Venue;
import vn.siliconstraits.nicestop.network.NetworkCallback;
import vn.siliconstraits.nicestop.network.NetworkErrorCallback;
import vn.siliconstraits.nicestop.network.NetworkHandler;

public class DataLayerMobileListenerService extends BaseDataLayerListenerService {
    private static final String TAG = DataLayerMobileListenerService.class.getSimpleName();

    private void onMessageGetNearbyVenues(String lat, String lng) {
        LogManager.logI(TAG, "onMessageGetNearbyVenues");
        NetworkHandler.getFoursquareLocations(lat, lng, this, new NetworkCallback() {
            @Override
            public void callback(Object object) {
                // send to wearable
                try {
                    List<Venue> objects = (List<Venue>) object;
                    // Venue => Venue mobile
                    List<VenueMobile> venueMobiles = new ArrayList<VenueMobile>();
                    for (Venue venue : objects) {
                        String name = venue.getName();
                        String address = venue.getLocation().getAddress();
                        VenueMobile venueMobile = new VenueMobile();
                        venueMobile.setName(name);
                        venueMobile.setAddress(address);
                        venueMobiles.add(venueMobile);

                        // download image and save to ??
                    }

                    // wrap and send to wear
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(baos);
                    for (VenueMobile venueMobile : venueMobiles) {
                        out.writeObject(venueMobile);
                    }
                    byte[] bytes = baos.toByteArray();
                    sendToPairDevice(Constant.PATH_GET_NEARBY_VENUES, bytes, null);
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
                        sendToPairDevice(String.format("response %d %d %f %f %d", y, x, latitude, longitude, googleZoom), outdata, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    LogManager.logE(TAG, "onMessageGet: exception:", e);
                }
            }
        }).start();
    }

    @Override
    protected void onMessageReceivedSuccess(MessageEvent m) {
        String requestType = m.getPath();
        Scanner scanner = new Scanner(new String(m.getData()));

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
            double lat = scanner.nextDouble();
            double lng = scanner.nextDouble();
            onMessageGetNearbyVenues(Double.toString(lat), Double.toString(lng));
        }
    }
}
