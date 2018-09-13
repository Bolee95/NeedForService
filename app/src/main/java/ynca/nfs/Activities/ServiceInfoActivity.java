package ynca.nfs.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.IOException;

import ynca.nfs.Activities.clientActivities.FeedbackActivity;
import ynca.nfs.Activities.clientActivities.ServiceRequestActivity;
import ynca.nfs.Models.Review;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;

public class ServiceInfoActivity extends AppCompatActivity {

    private ImageView serviceImage;
    private TextView serviceName;
    private TextView serviceLocation;
    private TextView serviceCity;
    private TextView serviceNumber;
    private RatingBar serviceRating;
    private TextView numberOfReviews;
    private ImageButton toMapButton;
    private Button requestService;
    private Button reviewService;
    private TextView distance;
    private MenuItem editButton;

    private Bitmap TempImage;
    private ProgressDialog mProgressDialog;

    private Intent currentIntent;
    private VehicleService currentService;
    private Boolean editable;
    private Boolean addedByUser;
    private Boolean enableImage;
    private float distanceRedirected;
    private boolean hideButtons;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private static final int PHOTO_PICKER =  2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_service);

        serviceNumber = (TextView) findViewById(R.id.serviceNumber);
        serviceImage = (ImageView) findViewById(R.id.serviceImage);
        serviceName = (TextView) findViewById(R.id.nameOfService);
        serviceLocation = (TextView) findViewById(R.id.serviceLocation);
        serviceCity = (TextView) findViewById(R.id.city);
        serviceRating = (RatingBar) findViewById(R.id.serviceRating);
        numberOfReviews = (TextView) findViewById(R.id.numberOfReviews);
        toMapButton = (ImageButton) findViewById(R.id.toMapButton);
        requestService = (Button) findViewById(R.id.requestService);
        reviewService = (Button) findViewById(R.id.reviewService);
        distance = (TextView) findViewById(R.id.distanceAway);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        serviceRating.setEnabled(false);

        currentIntent = getIntent();
        editable = currentIntent.getBooleanExtra("editable",false);
        addedByUser = currentIntent.getBooleanExtra("addedByUser", false);
        distanceRedirected = currentIntent.getFloatExtra("distance", 0);
        hideButtons = currentIntent.getBooleanExtra("hideButton", false);
        enableImage = currentIntent.getBooleanExtra("enableImage",false);

        //podesavanje toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.serviceInfoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if(hideButtons)
        {
            reviewService.setVisibility(View.INVISIBLE);
            requestService.setVisibility(View.INVISIBLE);
            toMapButton.setVisibility(View.INVISIBLE);
            //numberOfReviews.setVisibility(View.INVISIBLE);
        }


        //Uzima is Shared servis koji treba da se prikaze
        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("infoService","");
        currentService = gson.fromJson(json, VehicleService.class);


        initFields();

        requestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toServiceIntent = new Intent(ServiceInfoActivity.this,ServiceRequestActivity.class);
                toServiceIntent.putExtra("serviceUid",currentService.getUID());
                startActivity(toServiceIntent);
            }
        });

        reviewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toServiceIntent = new Intent(ServiceInfoActivity.this,FeedbackActivity.class);
                toServiceIntent.putExtra("uidOfReviewedService",currentService.getUID());
                startActivity(toServiceIntent);
            }
        });


        if(enableImage)
        {
            serviceImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PHOTO_PICKER);
                }
            });
        }
    }


    private void initFields()
    {
        serviceName.setText(currentService.getName());
        serviceCity.setText(currentService.getCity());
        serviceLocation.setText(currentService.getAddress());
        serviceNumber.setText(currentService.getPhoneNumber());
        if(addedByUser)
        {
            serviceRating.setVisibility(View.INVISIBLE);
        }
        else
        {
            serviceRating.setVisibility(View.VISIBLE);
        }


        //TODO: Napraviti da se automatski updateuje rating, postaviti listener na bazu
        if (currentService.getReviews() != null)
        {
            int reviewCount = 0;
            float avgGrade = 0;
            for (Review temp :
                    currentService.getReviews().values()) {
                ++reviewCount;
                avgGrade+= temp.getRate();
                avgGrade/= reviewCount;
            }
            numberOfReviews.setText(String.valueOf(reviewCount) + " reviews");
            serviceRating.setRating(avgGrade);
        }
        else
        {
            numberOfReviews.setText("0 reviews");
            serviceRating.setRating(0);

        }

        if (distanceRedirected != 0)
        {
            distance.setText(distanceRedirected + " away!");
        }
        else
        {
            distance.setText("");
        }

        if (currentService.getAddedByUser())
        {
            requestService.setVisibility(View.INVISIBLE);
            reviewService.setVisibility(View.INVISIBLE);
            serviceRating.setVisibility(View.INVISIBLE);
        }


        StorageReference photoRef = mStorageReference.child("photos").child(currentService.getUID());
        photoRef.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    //showProgressDialog();
                    Glide.with(serviceImage.getContext())
                            .load(uri).into(serviceImage);
                    //hideProgressDialog();
                }
                else {
                    serviceImage.setImageDrawable(getResources().getDrawable(R.drawable.sport_car_logos));
                }
            }
        });
        photoRef.getDownloadUrl().addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                serviceImage.setImageDrawable(getResources().getDrawable(R.drawable.sport_car_logos));
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.current_client_info_menu, menu);

        editButton = menu.findItem(R.id.editClientInfo);
        if (!editable)
        {
            editButton.setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.editClientInfo)
        { }
        else
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            try {
                TempImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            }
            catch(IOException ex)
            {}
            StorageReference photoRef = mStorageReference.child("photos").child(currentService.getUID());

            showProgressDialog();
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri s = taskSnapshot.getUploadSessionUri();
                    serviceImage.setImageBitmap(TempImage);
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
