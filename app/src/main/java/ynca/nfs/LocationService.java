package ynca.nfs;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ynca.nfs.Activities.mainScreensActivities.mainScreenClientActivity;
import ynca.nfs.Models.Client;
import ynca.nfs.Models.VehicleService;

public class LocationService extends Service {
    //private static final String TAG = "Lokacija";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0;
    private static final float CLIENT_SERVICE_DISTANCE = 100;

    private Location mLastClientLocation;
    private Location mServiceLocation;
    private Client currentClient;
    private ArrayList<VehicleService> mVehicleServices;
    private VehicleService toRemove; //kada se pojavi notifikacija za neki servis, taj servis se dodaje ovde i on se brise; da se ne bi ponovo pojavila notifikacija
    private Map<String,Object> childUpdate; //koristi se za update lokacije
    private ChildEventListener mChildEventListener;

    private NotificationCompat.Builder mBuilder;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference; //za klijente
    private DatabaseReference mDatabaseReference2; //za servise


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLastClientLocation.set(location);

            currentClient.setLastKnownLat(mLastClientLocation.getLatitude());
            currentClient.setLastKnownlongi(mLastClientLocation.getLongitude());

            childUpdate.put("lastKnownLat",mLastClientLocation.getLatitude());
            childUpdate.put("lastKnownlongi",mLastClientLocation.getLongitude());
            //upis u bazu
            mDatabaseReference.child(currentClient.getUID()).updateChildren(childUpdate);

            for (VehicleService vs : mVehicleServices) {
                mServiceLocation.setLatitude(vs.getLat());
                mServiceLocation.setLongitude(vs.getLongi());

                if (mLastClientLocation.distanceTo(mServiceLocation) < CLIENT_SERVICE_DISTANCE) {
                    //Toast.makeText(getApplicationContext(), "Servis je u blizini!", Toast.LENGTH_SHORT).show();

                    //notifikacija
                    mBuilder.setContentTitle("Auto servis " + vs.getName() + " se nalazi u vasoj blizini");

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(1, mBuilder.build());

                    toRemove = vs;

                }
            }
            mVehicleServices.remove(toRemove);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                            mLocationListener);
                } catch (java.lang.SecurityException ex) {
                    //Log.i(TAG, "fail to request location update, ignore", ex);
                } catch (IllegalArgumentException ex) {
                    //Log.d(TAG, "gps provider does not exist " + ex.getMessage());
                }
                Looper.loop();
            }
        }).start();


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mLastClientLocation = new Location("");
        mServiceLocation = new Location("");
        currentClient = new Client();
        mVehicleServices = new ArrayList<>();
        toRemove = new VehicleService();
        childUpdate = new HashMap<>();
        initializeLocationManager();

        //uzimanje ulogovanog klijenta
        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("currentClient","");
        currentClient = gson.fromJson(json, Client.class);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client");
        mDatabaseReference2 = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService");

        //builder za notifikaciju
        Intent intent = new Intent(getApplicationContext(), mainScreenClientActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.sport_car_logos)
                .setContentText("Klikom na notifikaciju otvoricete aplikaciju.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                VehicleService vs = dataSnapshot.getValue(VehicleService.class);
                mVehicleServices.add(vs);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabaseReference2.addChildEventListener(mChildEventListener);
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
        mDatabaseReference2.removeEventListener(mChildEventListener);
        stopSelf();
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
