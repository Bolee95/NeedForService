package ynca.nfs.Activities.clientActivities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;

public class addNewServiceActivity  extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private CheckBox useCurrentLocation;
    private SupportMapFragment mMapFragment;
    private Geocoder mGeocoder;
    private Address addressOfService;
    private EditText newServiceName;
    private EditText newServicePhone;
    private EditText newServiceEmail;

    private Button addNewServiceButton;
    private Intent intent;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private LatLng newServiceCoord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_service);


        Toolbar toolbar = (Toolbar) findViewById(R.id.newServiceToolbar);
        useCurrentLocation = (CheckBox) findViewById(R.id.checkBoxLocation);
        mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.newServiceMap));
        mGeocoder = new Geocoder(getApplicationContext());
        newServiceName = (EditText) findViewById(R.id.newServiceName);
        newServiceEmail = (EditText) findViewById(R.id.newServiceEmail);
        newServicePhone = (EditText) findViewById(R.id.newServicePhone);
        addNewServiceButton = (Button) findViewById(R.id.addNewServiceButton);


        intent = getIntent();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        String uid = intent.getStringExtra("uid");
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client").child(uid).child("listOfAddedServices");

        addNewServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VehicleService newService = new VehicleService();
                newService.setName(newServiceName.getText().toString());
                newService.setEmail(newServiceEmail.getText().toString());
                newService.setPhoneNumber(newServicePhone.getText().toString());
                if (useCurrentLocation.isChecked())
                {
                    Double lon = intent.getDoubleExtra("long", 0);
                    Double lat = intent.getDoubleExtra("lat", 0);
                    newService.setLongi(lon);
                    newService.setLat(lat);
                }
                else
                {
                    newService.setLat(newServiceCoord.latitude);
                    newService.setLongi(newServiceCoord.longitude);
                }


                try {
                    addressOfService = mGeocoder.getFromLocation(newService.getLat(),newService.getLongi(),1).get(0);
                    newService.setAddress(addressOfService.getThoroughfare() + " " + addressOfService.getFeatureName());
                    newService.setCity(addressOfService.getLocality());
                } catch (IOException e) {
                    newService.setAddress("");
                }
                Toast.makeText(getApplicationContext(),"New service has been added!",Toast.LENGTH_LONG).show();
                String key =mDatabaseReference.push().getKey();
                newService.setUID(key);
                newService.setAddedByUser(true);
                mDatabaseReference.child(key).setValue(newService);

                finish();
            }
        });

        useCurrentLocation.setChecked(true);
        getSupportFragmentManager().beginTransaction().hide(mMapFragment).commit();
        useCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (useCurrentLocation.isChecked()) {

                    getSupportFragmentManager().beginTransaction().hide(mMapFragment).commit();

                }
                else
                {
                    getSupportFragmentManager().beginTransaction().show(mMapFragment).commit();


                }
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.newServiceMap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                mMap.clear();
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(arg0.latitude,arg0.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                newServiceCoord = arg0;


                try {
                    addressOfService = mGeocoder.getFromLocation(arg0.latitude,arg0.longitude,1).get(0);
                    Toast.makeText(getApplicationContext(),  addressOfService.getThoroughfare() + " " + addressOfService.getFeatureName() + "," + addressOfService.getLocality(),Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });




        Double lon = intent.getDoubleExtra("long", 0);
        Double lat = intent.getDoubleExtra("lat", 0);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        CameraUpdate locationUpdated = CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat,lon), 15);
        mMap.animateCamera(locationUpdated);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();

        return super.onOptionsItemSelected(item);

    }

}
