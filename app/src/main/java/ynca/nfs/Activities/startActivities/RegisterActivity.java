package ynca.nfs.Activities.startActivities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import ynca.nfs.Activities.mainScreensActivities.MainScreenServisActivity;
import ynca.nfs.Activities.mainScreensActivities.mainScreenClientActivity;
import ynca.nfs.Models.Klijent;
import ynca.nfs.Models.Servis;
import ynca.nfs.R;

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
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));

        //ovo je za tabove
        host = (TabHost) findViewById(R.id.TabRegistration);

        host.setup();
        TabSpec ClientTab = host.newTabSpec("Klijent");
        ClientTab.setIndicator(getResources().getString(R.string.Client), getResources().getDrawable(android.R.drawable.star_on));
        ClientTab.setContent(R.id.Client);
        host.addTab(ClientTab);

        TabSpec ServiceTab = host.newTabSpec("Service");
        ServiceTab.setIndicator(getResources().getString(R.string.Service),getResources().getDrawable(android.R.drawable.star_on));
        ServiceTab.setContent(R.id.Service);
        host.addTab(ServiceTab);

        host.setCurrentTabByTag("Klijent");

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

        //buttons
        SubmitClient = (Button) findViewById(R.id.ButtonClientSubmit);
        SubmitService = (Button) findViewById(R.id.ButtonServiceSubmit);

        SubmitClient.setOnClickListener(this);
        SubmitService.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
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
        if(host.getCurrentTabTag().equals("Klijent")){
            if(user != null){
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Klijent klijent = new Klijent(mNameClient.getText().toString(),
                        mSurnameClient.getText().toString(),
                        mNumberClient.getText().toString(),
                        mEmailViewClient.getText().toString(),
                        user.getUid());
                mDatabase.child("Korisnik").child("Klijent").child(user.getUid()).setValue(klijent);
                hideProgressDialog();
                startActivity(new Intent(getBaseContext(), mainScreenClientActivity.class));
            }
        } else if(host.getCurrentTabTag().equals("Service")){
            if(user != null){
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Servis service = new Servis(mNameService.getText().toString(),
                        mNameOwnerService.getText().toString(),
                        mAddressService.getText().toString(),
                        mNumberService.getText().toString(),
                        mEmailViewService.getText().toString(),
                        user.getUid());
                mDatabase.child("Korisnik").child("Service").child(user.getUid()).setValue(service);
                hideProgressDialog();
                startActivity(new Intent(getBaseContext(), MainScreenServisActivity.class));
            }
        }
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