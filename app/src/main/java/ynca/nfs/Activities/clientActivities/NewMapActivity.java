package ynca.nfs.Activities.clientActivities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

import ynca.nfs.Models.Client;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;

public class NewMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReference1;
    public static ArrayList<VehicleService> services;
    private ArrayList<LatLng> servicesCoords;
    private CameraPosition mCameraPosition;
    private HashMap<String,Client> listOfFriends; //postavi se listener na referencu na prijatelje trenutno ulogovanog korisnika
    // i onda se dodaju na mapu i osluskuju se sa listenerom za promene

    private Location mLastKnownLocation;
    private LatLng mDefaultLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = Map_activity.class.getSimpleName();
    public boolean mLocationPermissionGranted;
    private boolean firstTimeLocated = true;
    private Client currentClient;

    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService");
        mDatabaseReference1 = mFirebaseDatabase.getReference().child("Korisnik").child("Client");
        services = new ArrayList<VehicleService>();

        //Uzimanje uloovanog korisnika
        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("TrenutniKlijent","");
        currentClient = gson.fromJson(json, Client.class);
        mDefaultLocation = new LatLng(currentClient.getLastKnownLat(),currentClient.getLastKnownlongi());

        mLastKnownLocation = new Location("");
        mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
        mLastKnownLocation.setLatitude(mDefaultLocation.latitude);

        //Toolbar podesavanja
        Toolbar toolbar = (Toolbar) findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ovo dugme ce da vodi na augmented reality mod
            }
        });


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Marker marker;
                VehicleService temp = dataSnapshot.getValue(VehicleService.class);
                services.add(temp);

                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLongi(), temp.getLat()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //services.remove(dataSnapshot.getValue(VehicleService.class));
                //services.add(dataSnapshot.getValue(VehicleService.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }

        ;
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.newServiceItem)
        {
            Intent newService = new Intent(this,addNewServiceActivity.class);
            newService.putExtra("long",(Double) mLastKnownLocation.getLongitude());
            newService.putExtra("lat",(Double) mLastKnownLocation.getLatitude());
            newService.putExtra("uid",(String) currentClient.getUID());
            startActivityForResult(newService,1);
            //Pokrece se intent sa opcijama za dodavanje novog servisa na trenutnoj lokaciji
        }
        else if (id == R.id.switchNightMode)
        {
            //Night mode switch
        }
        else if (id == R.id.switchFriendsView)
        {
            //dodaje markere sa prijateljima i njihove thumbnaile
        }
        else if (id == R.id.mapSearch)
        {

           //otvara formu za pretrazivanje po odredjenom atributu i onda pozicionira kameru u zavisnosti od izabranog
            // ili na osnovu unetog radiusa
        }
        else
        {
            finish();
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(this,"New service added!",Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDeviceLocation();
        updateLocationUI();

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mLastKnownLocation = location;
            if(firstTimeLocated) {
                CameraUpdate locationUpdated = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()), 15);
                mMap.animateCamera(locationUpdated);
                firstTimeLocated = false;
            }

            currentClient.setLastKnownLat(mLastKnownLocation.getLatitude());
            currentClient.setLastKnownlongi(mLastKnownLocation.getLongitude());
            mDatabaseReference1.child(currentClient.getUID()).setValue(currentClient);


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 1,
                    (float) 0.1, mLocationListener);
        }

        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mLastKnownLocation = null;
        }
    }

}
