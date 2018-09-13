package ynca.nfs.Activities.clientActivities;

import android.location.Location;
import android.location.LocationListener;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ynca.nfs.LocationProvider;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;

public class ARActivity extends AppCompatActivity {

    private ArchitectView architectView;
    private LocationProvider locationProvider;
    public static ArrayList<VehicleService> services;
    private VehicleService tempService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        this.architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setFeatures(ArchitectStartupConfiguration.Features.Geo);
        config.setLicenseKey("qw+nF2XMhcBF45qQjEZBJ8fAgzEF2kuJbM0qiLu/dM5TatM9UywBQgm8C9tBtPV9Tm1gRMoGJyKXz2vS1ndneBV0caY6TcdpFU6yIW6ZLphTa93LeZjqF+/9r3+cJWvM1U0RIeoBWEKQIaxYXbEaSKAlLx6XXYgsfxSfgHc7uf5TYWx0ZWRfX0KOcN8rBQsTRKX78TCO0GVHXhaE8uddXl/+AMWA9HpHGzt1gXH2Pf6ZjKl04D6bcaErIsgTU0qJVnei53zaV/ibiDuN+MlPP2lzyfSYy+gwW8dIpBa1S2wbjpjDIbtaSrAWPFnayLSd3OW62szkCTKJFolbufMW/CiEBWjHlUSJBRkHNOTaRVqrwhY4+hpDnL1Ll0zZX57JTB7SvD1aBq0Lwre2ZraXl+jOGUyg2b248DfjiyEn39SGGkxBDc4DL5MxXzPe0TSAy+bs7Oul1X+lJ2hP1JdQnw5X3cQZNXjjOcXl5ij0Ve8OeWFnx+4dHcSb/Pj3NBuRzG8mh7mOXp+YM8/G2yPofdsYOIPmwHSHTD/luIR0sMidVcFQO58iA+LZKRLgQi68IR79qGFQvhCwSKAllww8qbYXGZn/bKjT4HuxOS/dI2Mxu3A1kclwT4zPC+x5EjbQRGHkNl5WMKOA9IqY96BaTx5vzjzSh4H+CMAsh3e24zB8XN/n8/2yCi7fFP78UVoo5TVEFSbX2QpWYcf+Czw8/w==");

        this.architectView.onCreate(config);

        services = new ArrayList<>();
        services = NewMapActivity.ARservices;


        final JSONArray jsonArray = generatePoiInformation(services);
        generateJsonLocationFile(jsonArray);

        locationProvider = new LocationProvider(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location!=null && ARActivity.this.architectView != null ) {
                    // check if location has altitude at certain accuracy level & call right architect method (the one with altitude information)
                    if ( location.hasAltitude() && location.hasAccuracy() && location.getAccuracy()<7) {
                        ARActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy() );
                    } else {
                        ARActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
                    }
                }
            }

            @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override public void onProviderEnabled(String s) {}
            @Override public void onProviderDisabled(String s) {}
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.architectView.onPostCreate();

        try {
            this.architectView.load("file:///android_asset/demo2/index.html"); //TODO
        } catch (Exception ex) {}
    }

    public void generateJsonLocationFile(JSONArray json) {
        String fileContents = "var myJsonData = " + json.toString() + ";";
        try {
            File dataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "myjsondata.js");
            FileOutputStream stream = new FileOutputStream(dataFile, false);
            stream.write(fileContents.getBytes());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    private static JSONArray generatePoiInformation(ArrayList<VehicleService> lista) {

        final JSONArray pois = new JSONArray();

        final String ATTR_ID = "id";
        final String ATTR_NAME = "name";
        final String ATTR_OWNERS_NAME = "ownersName";
        final String ATTR_NUMBER = "number";
        final String ATTR_EMAIL = "email";
        final String ATTR_ADDRESS = "address";
        final String ATTR_LATITUDE = "latitude";
        final String ATTR_LONGITUDE = "longitude";
        final String ATTR_ALTITUDE = "altitude";
        final float UNKNOWN_ALTITUDE = -32768f; // equals "AR.CONST.UNKNOWN_ALTITUDE" in JavaScript (compare AR.GeoLocation specification)

        for (int i = 0; i < lista.size(); i++)
        {
            final HashMap<String, String> poiInformation = new HashMap<String, String>();
            poiInformation.put(ATTR_ID, lista.get(i).getUID());
            poiInformation.put(ATTR_NAME, lista.get(i).getName());
            poiInformation.put(ATTR_NUMBER, lista.get(i).getPhoneNumber());
            poiInformation.put(ATTR_OWNERS_NAME, lista.get(i).getOwnersName());
            poiInformation.put(ATTR_EMAIL, lista.get(i).getEmail());
            poiInformation.put(ATTR_ADDRESS, lista.get(i).getAddress());
            poiInformation.put(ATTR_LATITUDE, String.valueOf(lista.get(i).getLat()));
            poiInformation.put(ATTR_LONGITUDE, String.valueOf(lista.get(i).getLongi()));
            poiInformation.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));

            pois.put(new JSONObject(poiInformation));
        }

        return pois;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.architectView.onResume();
        // start location updates
        locationProvider.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.architectView.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

        this.architectView.onPause();
        // stop location updates
        locationProvider.onPause();
    }
}
