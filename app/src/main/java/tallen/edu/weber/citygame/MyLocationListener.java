package tallen.edu.weber.citygame;

/**
 * Created by Tyler on 3/19/2015.
 */

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    final String _logTag = "Monitor Location";
    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        float accuracy = location.getAccuracy();
        long time = location.getTime();

        //do work here
        Log.d(_logTag, "Monitor Location:" + "Provider: " + provider + "lat: " + lat + "long : " + lng + "time: " + time + "Acc: " + accuracy);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(_logTag, "Monitor Location - provider Enabled:" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(_logTag, "Monitor Location - provider Disabled:" + provider);
    }
}
