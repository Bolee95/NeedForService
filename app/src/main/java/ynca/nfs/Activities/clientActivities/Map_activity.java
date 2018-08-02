package ynca.nfs.Activities.clientActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ynca.nfs.Activities.GMapV2Direction;
import ynca.nfs.R;
import ynca.nfs.Models.Servis;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class Map_activity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    public static ArrayList<Servis> servisi;
    private ArrayList<LatLng> coorServisi;

    private static final String TAG = Map_activity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private Marker marker;
    public static LatLng markerCoor;
    public static ConnectivityManager conMng;
    public Button switchMode;
    public Button findService;

    private GoogleApiClient mGoogleApiClient;

    private final LatLng mDefaultLocation = new LatLng(43.331310, 21.892481);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        // kada se vrati u aplikaciju vraca se na prethodno mesto i postavlja kameru
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Servis");
        servisi = new ArrayList<Servis>();
        coorServisi = new ArrayList<LatLng>();

        setContentView(R.layout.activity_map_activity);
        switchMode = (Button) findViewById(R.id.MapSwitch);
        findService = (Button) findViewById(R.id.findMap);
        findService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionOnClosest();
            }
        });

        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sw = switchMode.getText().toString();
                if (sw.equals(getResources().getString(R.string.NightMode))) {
                    try {

                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                                getBaseContext(), R.raw.style_night));
                        switchMode.setText(getResources().getString(R.string.normal));

                        if (!success) {
                            Log.e(TAG, "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e(TAG, "Can't find style. Error: ", e);
                    }
                } else {
                    try {
                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                                getBaseContext(), R.raw.style_normal));
                        switchMode.setText(getResources().getString(R.string.NightMode));

                        if (!success) {
                            Log.e(TAG, "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e(TAG, "Can't find style. Error: ", e);
                    }
                }
            }
        });


        conMng = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Build the Play services client for use by the Fused Location Provider and the Places API.

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)

                .build();
        mGoogleApiClient.connect();

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                servisi.add(dataSnapshot.getValue(Servis.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // Pravi se mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //    getMenuInflater().inflate(R.menu.curent_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    //Aktivira se kad je mapa spremna
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMarkerClickListener(this);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        updateLocationUI();
        getDeviceLocation();

        while(servisi.size() == 0)
        {}

        UpdateMapMarkers();
    }

    //Hvata trenutnu poziciju
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
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
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

    //Hendler
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {

            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {

                    mMap.addMarker(new MarkerOptions()
                            .title(getString(R.string.default_info_title))
                            .position(mDefaultLocation)
                            .snippet(getString(R.string.default_info_snippet)));
                    // }
                }
            });
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
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    private void UpdateMapMarkers()
    {
    for (int i = 0; i < servisi.size(); i++) {
        try {
            LatLng temp = vratiKoordinate(getBaseContext(), servisi.get(i).getAdresa());
            if (temp != null) {
                servisi.get(i).setLongi(temp.longitude);
                servisi.get(i).setLat(temp.latitude);
                coorServisi.add(temp);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(temp)
                       // .title(servisi.get(i).getNaziv())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                //marker.showInfoWindow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        mMap.clear();
        final LatLng x = marker.getPosition();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.navigation))
                .setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Servis Temp = new Servis();
                        for (int i=0;i<servisi.size();i++)
                        {
                            if(servisi.get(i).getLongi() == marker.getPosition().longitude)
                                Temp = servisi.get(i);
                        }
                        if (Temp == null)
                        {
                            Temp.setNaziv("");
                        }
                        dialog.dismiss();
                       // mMap.clear();
                        mMap.addMarker(new MarkerOptions()
                                .position(x)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .title(Temp.getNaziv())).showInfoWindow();
                        GMapV2Direction md = new GMapV2Direction();


                        Document doc = md.getDocument(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), x, GMapV2Direction.MODE_DRIVING);
                        ArrayList<LatLng> directionPoint = null;

                        directionPoint = md.getDirection(doc);

                        if (switchMode.getText().toString().equals(getResources().getString(R.string.NightMode))) {
                            PolylineOptions rectLine = new PolylineOptions().width(7).color(
                                    Color.BLUE);

                            for (int i = 0; i < directionPoint.size(); i++) {
                                rectLine.add(directionPoint.get(i));

                            }

                            UpdateMapMarkers();

                            Polyline polylin = mMap.addPolyline(rectLine);
                        } else {
                            PolylineOptions rectLine = new PolylineOptions().width(7).color(
                                    Color.GREEN);

                            for (int i = 0; i < directionPoint.size(); i++) {
                                rectLine.add(directionPoint.get(i));

                            }
                           
                            UpdateMapMarkers();
                            Polyline polylin = mMap.addPolyline(rectLine);
                        }
                    }

                    //  }
                })
                .show();
        return true;
    }

    public LatLng vratiKoordinate(Context context, String adresa) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        LatLng temp;
        List<Address> addresses;
        addresses = geocoder.getFromLocationName(adresa, 1);
        if (addresses.size() > 0) {
            double latitude = addresses.get(0).getLatitude();
            double longitude = addresses.get(0).getLongitude();
            temp = new LatLng(latitude, longitude);

            return temp;

            //OVO SAD IDE U LISTU KOORDINATA IZ KOJE SE KASNIJE TRAZI NAJBLIZA TACKA
            //d = SQRT((x2 - x1)^2 + (y2 - y1)^2) <--- RACUNA RAZDALJINU

        } else
            return null;
    }

    public void positionOnClosest()
    {
        float dist = 10000000;
        int inx = 0;

        for(int i=0; i<coorServisi.size();i++) {
            float temp1 = (float) ((float) coorServisi.get(i).latitude - mLastKnownLocation.getLatitude())*(float) ((float) coorServisi.get(i).latitude - mLastKnownLocation.getLatitude());
            float temp2 = (float) ((float) coorServisi.get(i).longitude - mLastKnownLocation.getLongitude())*(float) ((float) coorServisi.get(i).latitude - mLastKnownLocation.getLatitude());

            float dis = (float) Math.sqrt(temp1 + temp2);
            if (dist > dis) {
                dist = dis;
                inx = i;
            }
        }

            for (int j=0; j<servisi.size();j++)
            {
                if (servisi.get(j).getLat() == coorServisi.get(inx).latitude)
                {

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(coorServisi.get(inx).latitude,coorServisi.get(inx).longitude), DEFAULT_ZOOM));
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(servisi.get(j).getLat(),servisi.get(j).getLongi()))
                            .title(getResources().getString(R.string.closestService)+"\n" + servisi.get(j).getNaziv())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    marker.showInfoWindow();
                }
            }





        }


    }




