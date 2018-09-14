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

import java.io.ByteArrayOutputStream;
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

                    //Intent galleryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //startActivityForResult(galleryIntent, PHOTO_PICKER);
                    showPictureDialog();

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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_18dp);
        getSupportActionBar().setTitle("");


        //Uzima is Shared servis koji treba da se prikaze
        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("clientInfo","");
        currentClient = gson.fromJson(json, Client.class);

        initFields();

        //region event Listeners

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
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.current_client_info_menu, menu);
        editButton = menu.findItem(R.id.editClientInfo);
        editButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent editProfile = new Intent(clientInfoActivity.this,editProfileActivity.class);
                startActivityForResult(editProfile,4);
                return  true;
            }
        });
        if (!isCurrentUser)
        {
            editButton.setVisible(false);
        }

        return true;
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                startActivityForResult(galleryIntent, 1);

                                break;
                            case 1:

                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, 2);

                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        //finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == 1) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);
                    mProfilePicture.setImageBitmap(bitmap);
                    Toast.makeText(clientInfoActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image = bitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(clientInfoActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == 2) {
            final Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            mProfilePicture.setImageBitmap(thumbnail);
            //Glide.with(mProfilePicture.getContext())
//               .load(thumbnail).into(mProfilePicture);

            StorageReference photoRef = mStorageReference.child("photos").child(currentClient.getUID());
            showProgressDialog();
            photoRef.putBytes(getBytes(thumbnail)).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri s = taskSnapshot.getUploadSessionUri();
                    mProfilePicture.setImageBitmap(thumbnail);
                    //Glide.with(mProfilePicture.getContext())
                    //        .load(s).into(mProfilePicture);
                    hideProgressDialog();
                }
            });
        }
            Toast.makeText(clientInfoActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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

    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
            return stream.toByteArray();
        }
        else
            return null;
    }
}