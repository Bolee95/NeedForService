package ynca.nfs.Activities.clientActivities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.Manifest;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
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
import ynca.nfs.SQLiteHelper;

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

    private DatabaseReference registeredServicesReference;
    private DatabaseReference friendsReference;
    private ChildEventListener ClientAddedServiceListener;
    private ChildEventListener servicesListener;
    private DatabaseReference clientAddedServicesReference;
    private ChildEventListener clientChildrenUpdateListener;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    public ArrayList<VehicleService> services;
    public static ArrayList<VehicleService> ARservices;
    private CameraPosition mCameraPosition;
    private ArrayList<Client> listOfFriends;
    private ArrayList<VehicleService> friendsServices;

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
    private SQLiteHelper cashe;

    LocationManager mLocationManager;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_map);
        mContext = this;
        cashe = new SQLiteHelper(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        filtersView = (CoordinatorLayout) findViewById(R.id.mapFilter);
        filtersView.setVisibility(View.INVISIBLE);
        searchResultView = (CoordinatorLayout) findViewById(R.id.mapSearchResultView);
        searchResultView.setVisibility(View.INVISIBLE);

        showFriendsMarkers = (CheckBox) findViewById(R.id.friendsMarkers);
        showFriends = (CheckBox) findViewById(R.id.friendsThumbnails);
        filterRadius = (EditText) findViewById(R.id.radiusFilter);
        filterRadius.setEnabled(false);
        showFriends.setChecked(true);
        showFriendsMarkers.setChecked(true);
        radiusFilterEnabled = (CheckBox) findViewById(R.id.radiusFilterEnabled);

        nightMode = false;
        listOfFriends = new ArrayList<Client>();
        friendsServices = new ArrayList<VehicleService>();
        ARservices = new ArrayList<VehicleService>();

        initRecycler();
        //region menu filter buttons
        filterRadius.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (radiusFilterEnabled.isChecked()) {
                    if (!filterRadius.getText().toString().equals("")) {
                        circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi()))
                                .radius(Float.parseFloat(filterRadius.getText().toString()))
                                .strokeColor(Color.BLUE)
                                .fillColor(R.color.radius));
                        filterMapWithRadius();
                    }

                } else {
                    filterMap();
                }
            }
        });


        //radiusFilterEnabled onClickListener
        radiusFilterEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radiusFilterEnabled.isChecked()) {
                    filterRadius.setEnabled(true);

                } else {
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
        //endregion
        //Uzimanje uloovanog korisnika
        SharedPreferences shared = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("currentClient", "");
        currentClient = gson.fromJson(json, Client.class);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        registeredServicesReference = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService");
        friendsReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client");
        clientAddedServicesReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client").child(currentClient.getUID()).child("listOfAddedServices");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        services = new ArrayList<VehicleService>();

        mDefaultLocation = new LatLng(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi());
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
                Intent ARIntent = new Intent(getApplicationContext(), ARActivity.class);
                startActivity(ARIntent);
            }
        });
        //region EventListeners
        ClientAddedServiceListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                VehicleService temp = dataSnapshot.getValue(VehicleService.class);
                //friendsServices.add(temp);
                services.add(temp);
                mAdapter.add(temp);

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
        clientAddedServicesReference.addChildEventListener(ClientAddedServiceListener);


        servicesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                VehicleService temp = dataSnapshot.getValue(VehicleService.class);
                services.add(temp);
                ARservices.add(temp);
                mAdapter.add(temp);
                mRecyclerView.setAdapter(mAdapter);


                //Marker marker = addServiceMarkerWithImage(temp);//mMap.addMarker(new MarkerOptions()
                        //.position(new LatLng(temp.getLat(), temp.getLongi()))
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                //marker.setTag(temp);
                addServiceMarkerWithImage(temp);
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

        registeredServicesReference.addChildEventListener(servicesListener);


        clientChildrenUpdateListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Client temp = dataSnapshot.getValue(Client.class);
                //Dodavanje prijatelja u listu i na mapi
                if (currentClient.getListOfFriendsUIDs() != null) {
                    if (currentClient.getListOfFriendsUIDs().containsValue(temp.getUID())) {
                        listOfFriends.add(temp);
                        addFriendsMarker(temp);
                        if (temp.getListOfAddedServices() != null)
                        for (VehicleService tempSer : temp.getListOfAddedServices().values())
                        {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(tempSer.getLat(), tempSer.getLongi()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setTag(temp); //clientAdded
                        }

                    }
                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Client temp = dataSnapshot.getValue(Client.class);
                if (temp.getUID().equals(currentClient.getUID())) {
                    //ako je promenjen broj servisa, treba da se registruje promena
                    if (currentClient.getServicesAdded() != temp.getServicesAdded()) {
                        currentClient.setServicesAdded(temp.getServicesAdded());
                        if (mMap != null)
                            filterMap();
                    }
                }
                //ukoliko je doslo do promene i ta promena je kod prijatelja
                if (currentClient.getListOfFriendsUIDs() != null && currentClient.getListOfFriendsUIDs().containsValue(temp.getUID())) {
                    for (Client friend :
                            listOfFriends) {
                        if (friend.getUID().equals(temp.getUID())) {
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
        friendsReference.addChildEventListener(clientChildrenUpdateListener);

        //da bi ar radio mora imati permission za cameru i lokaciju
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    11);
        }

        //endregion
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                         mMap.setMyLocationEnabled(true);
                } else {
                    mMap.setMyLocationEnabled(false);
                }
                return;
            }
        }
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void initRecycler() {
        mRecyclerView = (RecyclerView) findViewById(R.id.searchRecycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SearchResultAdapter(services, this);


    }

    private void filterMapWithRadius() {
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
                    Location.distanceBetween(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi(), temp.getLat(), temp.getLongi(), results);
                    if (results[0] < radius) {

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLat(), temp.getLongi()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(temp);
                    }
                } else {
                    Location.distanceBetween(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi(), temp.getLat(), temp.getLongi(), results);
                    if (results[0] < radius) {
                        addServiceMarkerWithImage(temp);
                        //mMap.addMarker(new MarkerOptions()
                        //        .position(new LatLng(temp.getLat(), temp.getLongi()))
                        //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).setTag(temp);
                    }
                }
            }

        }

        if (showFriendsMarkers.isChecked()) {
            for (Client friend : listOfFriends) {
                for (VehicleService temp :
                        friend.getListOfAddedServices().values()) {
                    Location.distanceBetween(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi(), temp.getLat(), temp.getLongi(), results);
                    if (results[0] < radius) {
                        //U zavisnosti da li li mogu da se prikazu servisi prijatelja
                        //friendsServices
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLat(), temp.getLongi()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(temp);
                    }
                }
            }

        }

        if (showFriends.isChecked())
        //U zavisnosti od toga da li je dozvoljeno da se prikazu prijatelji, uzimaju se njihove lokacije sa
        //listOfFriends se prikazuje
        {
            for (Client temp :
                    listOfFriends) {
                Location.distanceBetween(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi(), temp.getLastKnownLat(), temp.getLastKnownlongi(), results);
                if (results[0] < radius) {
                    addFriendsMarker(temp);
                }
            }
        }


    }
    private void addServiceMarkerWithImage(final VehicleService temp)
    {
        if (!cashe.imageExists(temp.getUID())) {
            StorageReference photoRef = mStorageReference.child("photos").child(temp.getUID());
            try {
                final File localFile = File.createTempFile(temp.getUID(), "");
                photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        String filePath = localFile.getPath();
                        Bitmap icon = BitmapFactory.decodeFile(filePath);

                        // ubacivanje u sql bazu
                        cashe.saveImage(temp.getUID(), icon);

                        Bitmap thumbnail = getRoundedCornerBitmap(Bitmap.createScaledBitmap(icon, 120, 120, false), 50);

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLat(), temp.getLongi()))
                                .icon(BitmapDescriptorFactory.fromBitmap(thumbnail)));
                        marker.setTag(temp);
                    }
                });

                photoRef.getFile(localFile).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.sport_car_logos);

                        Bitmap thumbnail = getRoundedCornerBitmap(Bitmap.createScaledBitmap(icon, 120, 120, false), 50);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLat(), temp.getLongi()))
                                .icon(BitmapDescriptorFactory.fromBitmap(thumbnail)));
                        marker.setTag(temp);
                    }
                });
            } catch (IOException ex) {
            }
        } else

        {
            Bitmap cashedImage = cashe.getImage(temp.getUID());
            if (cashedImage != null) {
                Bitmap thumbnail = getRoundedCornerBitmap(Bitmap.createScaledBitmap(cashedImage, 120, 120, false), 50);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLat(), temp.getLongi()))
                        .icon(BitmapDescriptorFactory.fromBitmap(thumbnail)));
                marker.setTag(temp);
            }
        }
    }

    private void addFriendsMarker(final Client temp) {
        if (!cashe.imageExists(temp.getUID())) {
            StorageReference photoRef = mStorageReference.child("photos").child(temp.getUID());
            try {
                final File localFile = File.createTempFile(temp.getUID(), "");
                photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        String filePath = localFile.getPath();
                        Bitmap icon = BitmapFactory.decodeFile(filePath);

                        // ubacivanje u sql bazu
                        cashe.saveImage(temp.getUID(), icon);
                        if (icon != null) {
                            Bitmap thumbnail = getRoundedCornerBitmap(Bitmap.createScaledBitmap(icon, 120, 120, false), 50);

                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(temp.getLastKnownLat(), temp.getLastKnownlongi()))
                                    .icon(BitmapDescriptorFactory.fromBitmap(thumbnail)));
                            marker.setTag(temp);
                        }
                    }
                });

                photoRef.getFile(localFile).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.sport_car_logos);

                        Bitmap thumbnail = getRoundedCornerBitmap(Bitmap.createScaledBitmap(icon, 120, 120, false), 50);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLastKnownLat(), temp.getLastKnownlongi()))
                                .icon(BitmapDescriptorFactory.fromBitmap(thumbnail)));
                        marker.setTag(temp);
                    }
                });
            } catch (IOException ex) {
            }
        } else

        {
            Bitmap cashedImage = cashe.getImage(temp.getUID());
            if (cashedImage != null) {
                Bitmap thumbnail = getRoundedCornerBitmap(Bitmap.createScaledBitmap(cashedImage, 120, 120, false), 50);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(temp.getLastKnownLat(), temp.getLastKnownlongi()))
                        .icon(BitmapDescriptorFactory.fromBitmap(thumbnail)));
                marker.setTag(temp);
            }
        }
    }


    //endregion



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
                    //add serviceMarkerWithImage
                    addServiceMarkerWithImage(temp);
                    //mMap.addMarker(new MarkerOptions()
                    //        .position(new LatLng(temp.getLat(), temp.getLongi()))
                    //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).setTag(temp);
                }
            }

        }


        if (showFriendsMarkers.isChecked()) {
            for (Client friend : listOfFriends) {
                if (friend.getListOfAddedServices() != null) {
                    for (VehicleService temp :
                            friend.getListOfAddedServices().values()) {
                        //U zavisnosti da li li mogu da se prikazu servisi prijatelja
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(temp.getLat(), temp.getLongi()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setTag(temp);
                    }

                }
            }
        }

        if (showFriends.isChecked())
        //U zavisnosti od toga da li je dozvoljeno da se prikazu prijatelji, uzimaju se njihove lokacije sa
        //listOfFriends se prikazuje
        {
            for (Client temp :
                    listOfFriends) {
                addFriendsMarker(temp);
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
        searchField.setQueryHint("Search by name or address...");

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
        clientIntent.putExtra("editable", false);
        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(client);
        prefEditor.putString("clientInfo",json);
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
        serviceIntent.putExtra("addedByUser",service.getAddedByUser());
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
            friendsReference.child(currentClient.getUID()).updateChildren(childUpdate);

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