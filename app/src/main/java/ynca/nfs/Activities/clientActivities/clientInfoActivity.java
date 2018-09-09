package ynca.nfs.Activities.clientActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.regex.Pattern;

import ynca.nfs.Activities.startActivities.LoginActivity;
import ynca.nfs.Activities.mainScreensActivities.mainScreenClientActivity;
import ynca.nfs.Models.Client;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;

public class clientInfoActivity  extends AppCompatActivity {
    private Button listCars;
    private TextView userEmail;
    private TextView lastKnownLocation;
    private TextView phoneNumber;
    private TextView NameAndSurname;
    private TextView servicesAdded;
    private MenuItem editButton;

    private Intent intent;
    private boolean isCurrentUser;
    private Geocoder mGeocoder;
    private Toolbar toolbar;

    private ImageView mProfilePicture;
    private ProgressDialog mProgressDialog;

    private static final int PHOTO_PICKER =  2;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private Client currentClient;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_client);


        listCars = (Button) findViewById(R.id.ButtonListCars);
        userEmail = (TextView) findViewById(R.id.EmailClientET);
        phoneNumber = (TextView) findViewById(R.id.NumberETClient);
        servicesAdded = (TextView) findViewById(R.id.servicesAddedClient);
        lastKnownLocation = (TextView) findViewById(R.id.lastKnownLocClientInfo);
        NameAndSurname = (TextView) findViewById(R.id.userName);
        mProfilePicture = (ImageView) findViewById(R.id.imageInfoClient);
        mGeocoder = new Geocoder(getApplicationContext());

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        intent = getIntent();
        isCurrentUser = intent.getBooleanExtra("editable", false);

        if(!isCurrentUser)
        {
            listCars.setVisibility(View.INVISIBLE);
        }

        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrentUser) {

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PHOTO_PICKER);

                }
            }
        });

        listCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ListaVozilaActivity.class));
            }
        });


        //Toolbar podesavanja
        toolbar = (Toolbar) findViewById(R.id.userInfoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");


        //Uzima is Shared servis koji treba da se prikaze
        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("clientInfo","");
        currentClient = gson.fromJson(json, Client.class);

        initFields();

        //region event Listeners

        //region komentarisano

        //SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        //SharedPreferences.Editor editor = shared.edit();
        //Gson gson = new Gson();
        //String json = shared.getString("TrenutniKlijent","");
        //Client trenutniKlijent = gson.fromJson(json, Client.class);

        //User.setText(trenutniKlijent.getFirstName() + " " + trenutniKlijent.getFirstName());
        //EmailClientET.setText(trenutniKlijent.getEmail());
        //PhoneNum.setText(trenutniKlijent.getFirstName());


        /*PhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNum.setText("");
            }
        });*/
//        PhoneNum.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ChangeSaveButtonState();
//                isNumberChanged = true;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (PhoneNum.getText().equals(""))
//                {
//                    //Vrati na stare podatke
//                    PhoneNum.setText("018/1234567");
//                }
//            }
//        });

        /*PasswordClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordClient.setText("");
            }
        });*/
//        PasswordClient.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ChangeSaveButtonState();
//                isPasswordChanged = true;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (PasswordClient.getText().equals(""))
//                {
//                    //Vrati na stare podatke
//                    PasswordClient.setText("password");
//                }
//
//            }
//        });
        /*EmailClientET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailClientET.setText("");
            }
        });*/
//        EmailClientET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ChangeSaveButtonState();
//                isEmailChanged = true;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(EmailClientET.getText().equals(""))
//                {
//                    //Vrati stare podatke
//                    EmailClientET.setText("stari podaci");
//                }
//            }
//        });

//            User.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ChangeSaveButtonState();
//                isNameChanged = true;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        //provera da li vec ima postojeca slika
        //endregion
        StorageReference photoRef = mStorageReference.child("photos").child(currentClient.getUID());
        photoRef.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    showProgressDialog();
                    Glide.with(mProfilePicture.getContext())
                            .load(uri).into(mProfilePicture);
                    hideProgressDialog();
                }
                else {
                    mProfilePicture.setImageDrawable(getResources().getDrawable(R.drawable.sport_car_logos));
                }

            }
        });
        photoRef.getDownloadUrl().addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProfilePicture.setImageDrawable(getResources().getDrawable(R.drawable.sport_car_logos));
            }
        });

    }


    private void initFields()
    {
        userEmail.setText(currentClient.getEmail());
        phoneNumber.setText(currentClient.getPhoneNumber());
        NameAndSurname.setText(currentClient.getFirstName() + " " + currentClient.getLastName());
        if (currentClient.getServicesAdded() == 0)
        {
            servicesAdded.setText("0");
        }
        else {
            String serviceAdded = String.valueOf(currentClient.getServicesAdded());
            servicesAdded.setText(serviceAdded);
        }


        Address address;
        try {
            address = mGeocoder.getFromLocation(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi(), 1).get(0);
            lastKnownLocation.setText(address.getLocality() + "," + address.getCountryName());
        }
        catch (IOException e)
        {
            lastKnownLocation.setText("Unknown");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(clientInfoActivity.this, mainScreenClientActivity.class));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.current_client_info_menu, menu);
        editButton = menu.findItem(R.id.editClientInfo);
        if (!isCurrentUser)
        {
            editButton.setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        finish();
        return super.onOptionsItemSelected(item);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            }
            catch(IOException ex)
            {}
            StorageReference photoRef = mStorageReference.child("photos").child(currentClient.getUID());

            showProgressDialog();
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri s = taskSnapshot.getUploadSessionUri();
                    mProfilePicture.setImageBitmap(image);
                    //Glide.with(mProfilePicture.getContext())
                    //        .load(s).into(mProfilePicture);
                    hideProgressDialog();
                }
            });
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
}