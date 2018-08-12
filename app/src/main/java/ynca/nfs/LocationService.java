package ynca.nfs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ynca.nfs.Models.Client;

public class LocationService extends Service {

    //private static final String TAG = "Lokacija";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0;
    private Location mLastClientLocation;
    private Location mVehicleServiceLocation;
    private Client currentClient;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;



    /*
    private class LocationListener implements android.location.LocationListener {
        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    */

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLastClientLocation.set(location);

            currentClient.setLastKnownLat(mLastClientLocation.getLatitude());
            currentClient.setLastKnownlongi(mLastClientLocation.getLongitude());

            Map<String,Object> childUpdate = new HashMap<>();
            childUpdate.put("lastKnownLat",mLastClientLocation.getLatitude());
            childUpdate.put("lastKnownlongi",mLastClientLocation.getLongitude());
            //upis u bazu
            mDatabaseReference.child(currentClient.getUID()).updateChildren(childUpdate);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
            //Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        return START_STICKY;
    }
    @Override
    public void onCreate() {
        mLastClientLocation = new Location("");
        mVehicleServiceLocation = new Location("");
        initializeLocationManager();

        //uzimanje ulogovanog klijenta
        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("TrenutniKlijent","");
        currentClient = gson.fromJson(json, Client.class);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client");

        /*
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
                    LOCATION_DISTANCE, mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                //Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
