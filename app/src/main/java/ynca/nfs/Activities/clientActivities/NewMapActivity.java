package ynca.nfs.Activities.clientActivities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.Toast;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ynca.nfs.Activities.ServiceInfoActivity;
import ynca.nfs.Adapter.SearchResultAdapter;
import ynca.nfs.Models.Client;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;
//TODO: Prvi put kad se ukljuci aplikacija i kada se da dozvola za lokaciju, ne prikazuje trenutnu lokaciju
public class NewMapActivity extends AppCompatActivity implements OnMapReadyCallback, SearchResultAdapter.OnItemsClickListener {

    //region properties
    private GoogleMap mMap;
    private CheckBox showFriendsMarkers;
    private CheckBox showFriends;
    private EditText filterRadius;
    private CoordinatorLayout filtersView;
    private CoordinatorLayout searchResultView;
    private CheckBox radiusFilterEnabled;

    private MenuItem searchItem;
    private SearchView searchField;
    private RecyclerView mRecyclerView;
    private SearchResultAdapter mAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference friendsDatabaseReference;
    private ChildEventListener friendsEventListener;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference1;
    private ChildEventListener mChildEventListener2;
    private ChildEventListener servicesListener;
    private DatabaseReference mDatabaseReference2;
    private ChildEventListener clientChildrenUpdateListener;

    public static ArrayList<VehicleService> services;
    private CameraPosition mCameraPosition;
    private ArrayList<Client> listOfFriends;
    private ArrayList<VehicleService> friendsServices;//postavi se listener na referencu na prijatelje trenutno ulogovanog korisnika
    // i onda se dodaju na mapu i osluskuju se sa listenerom za promene

    private Location mLastKnownLocation;
    private LatLng mDefaultLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    //private static final String TAG = Map_activity.class.getSimpleName();
    public boolean mLocationPermissionGranted;
    private boolean firstTimeLocated = true;
    private Client currentClient;
    private boolean nightMode;
    private Circle circle;
    private Context mContext;

    LocationManager mLocationManager;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_map);
        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        filtersView = (CoordinatorLayout) findViewById(R.id.mapFilter);
        filtersView.setVisibility(View.INVISIBLE);
        searchResultView = (CoordinatorLayout) findViewById(R.id.mapSearchResultView);
        searchResultView.setVisibility(View.INVISIBLE);

        showFriendsMarkers = (CheckBox) findViewById(R.id.friendsMarkers);
        showFriends = (CheckBox)  findViewById(R.id.friendsThumbnails);
        filterRadius = (EditText) findViewById(R.id.radiusFilter);
        filterRadius.setEnabled(false);
        showFriends.setChecked(true);
        showFriendsMarkers.setChecked(true);
        radiusFilterEnabled = (CheckBox) findViewById(R.id.radiusFilterEnabled);

        nightMode = false;
        listOfFriends = new ArrayList<Client>();
        friendsServices = new ArrayList<VehicleService>();


        initRecycler();

        filterRadius.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (radiusFilterEnabled.isChecked())
                {
                    if (!filterRadius.getText().toString().equals("") ) {
                        circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi()))
                                .radius(Float.parseFloat(filterRadius.getText().toString()))
                                .strokeColor(Color.BLUE)
                                .fillColor(R.color.radius));
                        filterMapWithRadius();
                    }

                }
                else
                {
                    filterMap();
                }
            }
        });


        //radiusFilterEnabled onClickListener
        radiusFilterEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radiusFilterEnabled.isChecked())
                {
                    filterRadius.setEnabled(true);

            }
                else {
                    filterRadius.setEnabled(false);
                    filterMap();
                }
            }
        });



        showFriendsMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMap();
            }
        });
        showFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMap();
            }
        });

        //Uzimanje uloovanog korisnika
        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("TrenutniKlijent","");
        currentClient = gson.fromJson(json, Client.class);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService");
        mDatabaseReference1 = mFirebaseDatabase.getReference().child("Korisnik").child("Client");
        mDatabaseReference2 = mFirebaseDatabase.getReference().child("Korisnik").child("Client").child(currentClient.getUID()).child("listOfAddedServices");
        friendsDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client").child(currentClient.getUID()).child("listOfFriends");
        services = new ArrayList<VehicleService>();


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
        //region EventListeners
        mChildEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Toast.makeText(getApplicationContext(),"EventListener Triggered",Toast.LENGTH_SHORT).show();
                VehicleService temp = dataSnapshot.getValue(VehicleService.class);
                services.add(temp);

                //if (!mAdapter.listContains(temp))
                //{
                    mAdapter.add(temp);
                //}


                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLat(), temp.getLongi()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.setTag(temp);
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

        mDatabaseReference2.addChildEventListener(mChildEventListener2);


        servicesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                VehicleService temp = dataSnapshot.getValue(VehicleService.class);
                services.add(temp);
                //if (!mAdapter.listContains(temp))
                //{
                mAdapter.add(temp);
                //}
                mRecyclerView.setAdapter(mAdapter);


                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLat(), temp.getLongi()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                marker.setTag(temp);

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

        mDatabaseReference.addChildEventListener(servicesListener);


        friendsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Client temp = dataSnapshot.getValue(Client.class);
                listOfFriends.add(temp);
                HashMap<String,VehicleService> tempList = temp.getListOfAddedServices();
                for (VehicleService tempService : tempList.values()
                        ) {
                    friendsServices.add(tempService);

                }
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLastKnownLat(), temp.getLastKnownlongi()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                marker.setTag(temp);
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

        friendsDatabaseReference.addChildEventListener(friendsEventListener);

        clientChildrenUpdateListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Client temp = dataSnapshot.getValue(Client.class);
                //Dodavanje prijatelja u listu i na mapi
                if (currentClient.getListOfFriendsUIDs() != null) {
                    if (currentClient.getListOfFriendsUIDs().containsValue(temp.getUID())) {
                        listOfFriends.add(temp);
                        //TODO: Srediti da se prikazuje thumbnail kao marker
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLastKnownLat(), temp.getLastKnownlongi()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        marker.setTag(temp);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Client temp = dataSnapshot.getValue(Client.class);
                if (temp.getUID().equals(currentClient.getUID()))
                {
                    //ako je promenjen broj servisa, treba da se registruje promena
                    if (currentClient.getServicesAdded() != temp.getServicesAdded()) {
                        currentClient.setServicesAdded(temp.getServicesAdded());
                        if (mMap != null)
                            filterMap();
                    }
                }
                //ukoliko je doslo do promene i ta promena je kod prijatelja
                if (currentClient.getListOfFriendsUIDs() != null && currentClient.getListOfFriendsUIDs().containsValue(temp.getUID()))
                {
                    for (Client friend:
                         listOfFriends) {
                        if (friend.getUID().equals(temp.getUID()))
                        {
                            //brise se iz liste i ubacuje se isti taj ali azurirani korisnik
                            listOfFriends.remove(friend);
                            listOfFriends.add(temp);
                            if (mMap != null)
                                filterMap();
                        }
                    }
                }
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
        mDatabaseReference1.addChildEventListener(clientChildrenUpdateListener);

        //endregion
    }



    private void initRecycler()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.searchRecycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SearchResultAdapter(services,this);


    }

    private void filterMapWithRadius()
    {
        mMap.clear();
        float[] results = new float[100];
        float radius = Float.parseFloat(filterRadius.getText().toString());
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi()))
                .radius(Float.parseFloat(filterRadius.getText().toString()))
                .strokeColor(R.color.colorAccent)
                .fillColor(R.color.radius));
        for (VehicleService temp : services
                ) {

            if (temp.getAddedByUser() != null) {
                if (temp.getAddedByUser() == true) {
                    Location.distanceBetween(currentClient.getLastKnownLat(),currentClient.getLastKnownlongi(),temp.getLat(),temp.getLongi(),results);
                    if (results[0] < radius) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLat(), temp.getLongi()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(temp);
                    }
                } else {
                    Location.distanceBetween(currentClient.getLastKnownLat(),currentClient.getLastKnownlongi(),temp.getLat(),temp.getLongi(),results);
                    if (results[0] < radius) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLat(), temp.getLongi()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).setTag(temp);
                    }
                }
            }

        }


        if (showFriendsMarkers.isChecked()) {
            for (VehicleService temp :
                    friendsServices) {
                Location.distanceBetween(currentClient.getLastKnownLat(),currentClient.getLastKnownlongi(),temp.getLat(),temp.getLongi(),results);
                if (results[0] < radius) {
                    //U zavisnosti da li li mogu da se prikazu servisi prijatelja
                    //friendsServices
                    //HashMap<String,VehicleService> tempList = temp.getListOfAddedServices();
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(temp.getLat(), temp.getLongi()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTag(temp);
                }
            }

        }

        if (showFriends.isChecked())
        //U zavisnosti od toga da li je dozvoljeno da se prikazu prijatelji, uzimaju se njihove lokacije sa
        //listOfFriends se prikazuje
        {
            for (Client temp :
                    listOfFriends) {
                Location.distanceBetween(currentClient.getLastKnownLat(),currentClient.getLastKnownlongi(),temp.getLastKnownLat(),temp.getLastKnownlongi(),results);
                if (results[0] < radius) {

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(temp.getLastKnownLat(), temp.getLastKnownlongi()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))).setTag(temp);
                }

            }
        }


    }

    private void filterMap() {
        mMap.clear();
        for (VehicleService temp : services
                ) {
            if (temp.getAddedByUser() != null) {
                if (temp.getAddedByUser() == true) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(temp.getLat(), temp.getLongi()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(temp);
                } else {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(temp.getLat(), temp.getLongi()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).setTag(temp);
                }
            }

        }


        if (showFriendsMarkers.isChecked()) {
            for (VehicleService temp :
                    friendsServices) {
                //U zavisnosti da li li mogu da se prikazu servisi prijatelja
                //friendsServices
                //HashMap<String,VehicleService> tempList = temp.getListOfAddedServices();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLat(), temp.getLongi()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTag(temp);
            }

        }

        if (showFriends.isChecked())
        //U zavisnosti od toga da li je dozvoljeno da se prikazu prijatelji, uzimaju se njihove lokacije sa
        //listOfFriends se prikazuje
        {
            for (Client temp :
                    listOfFriends) {

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLastKnownLat(), temp.getLastKnownlongi()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))).setTag(temp);

            }
        }


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.map_menu, menu);

        //region searchField event listeners and init
        searchItem = menu.findItem(R.id.mapSearchItem);
        searchField = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchField.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchResultView.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        searchField.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchResultView.getVisibility() == View.INVISIBLE)
                {
                    searchResultView.setVisibility(View.VISIBLE);
                }
            }
        });

        searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResultView.setVisibility(View.VISIBLE);
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
        //endregion
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
            startActivity(newService);
        }
        else if (id == R.id.switchNightMode)
        {
            if (!nightMode) {
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                        getBaseContext(), R.raw.style_night));
                nightMode = true;
            }
            else {
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                        getBaseContext(), R.raw.style_normal));
                nightMode = false;
            }
        }
        else if (id == R.id.filterMap)
        {
            if ( filtersView.getVisibility() == View.VISIBLE)
            {
                filtersView.setVisibility(View.INVISIBLE);
            }
            else
                filtersView.setVisibility(View.VISIBLE);


        }
        else if (id == R.id.mapSearchItem)
        {

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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Object temp = marker.getTag();

                if (temp instanceof VehicleService)
                {
                    redirectToServiceInfo((VehicleService)temp);
                }
                else if (temp instanceof Client)
                {
                    redirectToClientInfo((Client)temp);
                }

                return true;
            }
        });

        getDeviceLocation();
        updateLocationUI();

    }

    private void redirectToClientInfo(Client client)
    {
        Intent clientIntent = new Intent(this, clientInfoActivity.class);
        clientIntent.putExtra("editable", true);

        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(client);
        prefEditor.putString("clientInfo", json);
        prefEditor.commit();

        startActivity(clientIntent);
    }


    private void redirectToServiceInfo(VehicleService service)
    {
        Intent serviceIntent = new Intent(this, ServiceInfoActivity.class);
        //udaljenost servisa od korisnika
        float[] results = new float[10];
        Location.distanceBetween(service.getLat(),service.getLongi(),currentClient.getLastKnownLat(),currentClient.getLastKnownlongi(),results);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String result = String.valueOf(df.format(results[0]/1000));
        serviceIntent.putExtra("distance",result);
        serviceIntent.putExtra("editable",false);

        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(service);
        prefEditor.putString("infoService", json);
        prefEditor.commit();

        startActivity(serviceIntent);
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
            Map<String,Object> childUpdate = new HashMap<>();
            childUpdate.put("lastKnownLat",mLastKnownLocation.getLatitude());
            childUpdate.put("lastKnownlongi",mLastKnownLocation.getLongitude());
            mDatabaseReference1.child(currentClient.getUID()).updateChildren(childUpdate);



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
            mMap.setMyLocationEnabled(true);
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 10000,
                    (float) 5, mLocationListener);
        }

        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            //Log.d(TAG, "Current location is null. Using defaults.");
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


    //Onclick event za klik na neki od servisa onosno neke od slika na ekranu
    @Override
    public void OnItemClick(int clickItemIndex,VehicleService service) {
        searchResultView.setVisibility(View.INVISIBLE);
        CameraUpdate locationUpdated = CameraUpdateFactory.newLatLngZoom(
                new LatLng(service.getLat(),service.getLongi()), 15);
        mMap.animateCamera(locationUpdated);

        }

}
