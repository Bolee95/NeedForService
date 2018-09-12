package ynca.nfs.Activities.startActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ynca.nfs.Activities.mainScreensActivities.MainScreenServisActivity;
import ynca.nfs.Activities.mainScreensActivities.mainScreenClientActivity;
import ynca.nfs.Models.Client;
import ynca.nfs.R;
import ynca.nfs.Models.VehicleService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button SubmitClient;
    private Button SubmitService;
    private TabHost host;

    private EditText mEmailViewClient;
    private EditText mPasswordViewClient;
    private EditText mNameClient;
    private EditText mSurnameClient;
    private EditText mNumberClient;
    private EditText mEmailViewService;
    private EditText mPasswordViewService;
    private EditText mNameService;
    private EditText mNameOwnerService;
    private EditText mNumberService;
    private EditText mAddressService;
    private EditText mCityService;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Geocoder mGeocoder;
    List<Address> locations; // za dobijanje koordinata

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ovo je za tabove
        host = (TabHost) findViewById(R.id.TabRegistration);

        host.setup();
        TabSpec ClientTab = host.newTabSpec("Client");
        ClientTab.setIndicator(getResources().getString(R.string.Client), getResources().getDrawable(android.R.drawable.star_on));
        ClientTab.setContent(R.id.Client);
        host.addTab(ClientTab);

        TabSpec ServiceTab = host.newTabSpec("Service");
        ServiceTab.setIndicator(getResources().getString(R.string.Service),getResources().getDrawable(android.R.drawable.star_on));
        ServiceTab.setContent(R.id.Service);
        host.addTab(ServiceTab);

        host.setCurrentTabByTag("Client");

        //views
        mEmailViewClient = (EditText) findViewById(R.id.EditTextClientEmail);
        mPasswordViewClient = (EditText) findViewById(R.id.EditTextClientPassword);
        mEmailViewService = (EditText) findViewById(R.id.EditTextServiceEmail);
        mPasswordViewService = (EditText) findViewById(R.id.EditTextServicePassword);
        mNameClient = (EditText) findViewById(R.id.EditTextClientName);
        mSurnameClient = (EditText) findViewById(R.id.EditTextClientSurname);
        mNumberClient = (EditText) findViewById(R.id.EditTextClientNumber);
        mNameService = (EditText) findViewById(R.id.EditTextServiceName);
        mNameOwnerService = (EditText) findViewById(R.id.EditTextServiceNameOwner);
        mNumberService = (EditText) findViewById(R.id.EditTextServiceNumber);
        mAddressService = (EditText) findViewById(R.id.EditTextServiceAdresa);
        mCityService = (EditText) findViewById(R.id.EditTextCity);

        //geocoder init
        mGeocoder = new Geocoder(getApplicationContext());

        //buttons
        SubmitClient = (Button) findViewById(R.id.ButtonClientSubmit);
        SubmitService = (Button) findViewById(R.id.ButtonServiceSubmit);

        SubmitClient.setOnClickListener(this);
        SubmitService.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        locations = new List<Address>() {

            //region podesavanje liste
            @Override
            public int size() {
                return this.size();
            }

            @Override
            public boolean isEmpty() {
                if (this.size() == 0) {
                    return true;
                }
                else
                    return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<Address> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean add(Address address) {
                this.add(address);
                return true;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends Address> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends Address> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public Address get(int index) {
                return  this.get(index);

            }

            @Override
            public Address set(int index, Address element) {
                return null;
            }

            @Override
            public void add(int index, Address element) {

            }

            @Override
            public Address remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<Address> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<Address> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<Address> subList(int fromIndex, int toIndex) {
                return null;
            }
            //endregion
        };

    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.RegSucc) ,
                                    Toast.LENGTH_LONG).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.RegFail),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        //client tab provera
        if(host.getCurrentTabTag().equals(getResources().getString(R.string.Client))) {
            String email = mEmailViewClient.getText().toString();
            if (TextUtils.isEmpty(email)) {
                mEmailViewClient.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else if(!email.contains("@")){
                mEmailViewClient.setError("Invalid email.");
                valid = false;
            } else {
                mEmailViewClient.setError(null);
            }

            String password = mPasswordViewClient.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordViewClient.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else if(password.length() < 8){
                mPasswordViewClient.setError(getResources().getString(R.string.PassLength));
                valid = false;
            } else {
                mPasswordViewClient.setError(null);
            }

            String firstName = mNameClient.getText().toString();
            if(TextUtils.isEmpty(firstName)){
                mNameClient.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else {
                mNameClient.setError(null);
            }

            String lastName = mSurnameClient.getText().toString();
            if(TextUtils.isEmpty(lastName)){
                mSurnameClient.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else {
                mSurnameClient.setError(null);
            }

            String number = mNumberClient.getText().toString();
            if(TextUtils.isEmpty(number)){
                mNumberClient.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else {
                mNumberClient.setError(null);
            }
        }
        //service tab provera
        else if(host.getCurrentTabTag().equals("Service")){
            String email = mEmailViewService.getText().toString();
            if (TextUtils.isEmpty(email)) {
                mEmailViewService.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else if(!email.contains("@")){
                mEmailViewService.setError(getResources().getString(R.string.InvalidMail));
                valid = false;
            } else {
                mEmailViewService.setError(null);
            }

            String password = mPasswordViewService.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordViewService.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else if(password.length() < 8){
                mPasswordViewService.setError(getResources().getString(R.string.PassLength));
                valid = false;
            } else {
                mPasswordViewService.setError(null);
            }

            String nameService = mNameService.getText().toString();
            if(TextUtils.isEmpty(nameService)){
                mNameService.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else {
                mNameService.setError(null);
            }

            String ownerNameService = mNameOwnerService.getText().toString();
            if(TextUtils.isEmpty(ownerNameService)){
                mNameOwnerService.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else {
                mNameOwnerService.setError(null);
            }

            String numberService = mNumberService.getText().toString();
            if(TextUtils.isEmpty(numberService)){
                mNumberService.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else {
                mNumberService.setError(null);
            }

            String adresa = mAddressService.getText().toString();
            if(TextUtils.isEmpty(adresa)){
                mAddressService.setError(getResources().getString(R.string.Requested));
                valid = false;
            } else {
                mAddressService.setError(null);
            }
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if(host.getCurrentTabTag().equals("Client")){
            if(user != null){
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Client client = new Client(mNameClient.getText().toString(),
                        mSurnameClient.getText().toString(),
                        mNumberClient.getText().toString(),
                        mEmailViewClient.getText().toString(),
                        user.getUid());
                mDatabase.child("Korisnik").child("Client").child(user.getUid()).setValue(client);
                hideProgressDialog();
                startActivity(new Intent(getBaseContext(), mainScreenClientActivity.class));
                activateLocationService();
            }
        } else if(host.getCurrentTabTag().equals("Service")){
            if(user != null){
                mDatabase = FirebaseDatabase.getInstance().getReference();
                VehicleService vehicleService = new VehicleService(mNameService.getText().toString(),
                        mNameOwnerService.getText().toString(),
                        mAddressService.getText().toString(),
                        mNumberService.getText().toString(),
                        mEmailViewService.getText().toString(),
                        user.getUid());
                vehicleService.setAddedByUser(false);
                vehicleService.setCity(mCityService.getText().toString());

                //dodavanje koordinata

                try {
                    locations = mGeocoder.getFromLocationName(vehicleService.getAddress() + "," + vehicleService.getCity(), 1);
                }
                catch (IOException ex)
                {
                    vehicleService.setLongi(0);
                    vehicleService.setLongi(0);
                }
                if (!locations.isEmpty())
                {
                    vehicleService.setLongi(locations.get(0).getLongitude());
                    vehicleService.setLat(locations.get(0).getLatitude());
                }


                mDatabase.child("Korisnik").child("VehicleService").child(user.getUid()).setValue(vehicleService);
                hideProgressDialog();
                startActivity(new Intent(getBaseContext(), MainScreenServisActivity.class));
            }
        }
    }
    private void activateLocationService()
    {
        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putBoolean("locationServiceStatus", true);
        prefEditor.commit();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ButtonClientSubmit) {
            createAccount(mEmailViewClient.getText().toString(), mPasswordViewClient.getText().toString());
        } else if (i == R.id.ButtonServiceSubmit) {
            createAccount(mEmailViewService.getText().toString(), mPasswordViewService.getText().toString());
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}