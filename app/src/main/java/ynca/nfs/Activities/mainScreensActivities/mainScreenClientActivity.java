package ynca.nfs.Activities.mainScreensActivities;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ynca.nfs.Activities.ServiceInfoActivity;
import ynca.nfs.Activities.clientActivities.FriendsActivity;
import ynca.nfs.Activities.clientActivities.addVehicleFormActivity;
import ynca.nfs.Activities.clientActivities.clientInfoActivity;
import ynca.nfs.Activities.clientActivities.NewMapActivity;
import ynca.nfs.Activities.startActivities.LoginActivity;
import ynca.nfs.Activities.clientActivities.ServiceRequestActivity;
import ynca.nfs.Adapter.ItemListClientAdapter;
import ynca.nfs.LocationService;
import ynca.nfs.Models.Client;
import ynca.nfs.R;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.SQLiteHelper;

public class mainScreenClientActivity extends AppCompatActivity implements ItemListClientAdapter.OnItemsClickListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mChildEventListener;

    private DatabaseReference mDatabaseReference2;
    private ChildEventListener mChildEventListener2;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private static Client currentClient;
    private ArrayList<VehicleService> services;

    private static final int BROJ_PRIKAZANIH_ELEMENATA = 6;
    private ItemListClientAdapter adapter;
    private RecyclerView recycler;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private NavigationView navBar;
    private TextView NameAndSurr;
    private TextView Descript;
    private Switch serviceSwitch;

    //region NavBar Buttons Declarations
    private  Button newCar;
    private  Button testDugme;
    private Button mapView;
    private ImageView clientImage;
    private Button FriendsButton;
    private Button signOutBtn;
    private boolean userFetched;
    private boolean backgroundServiceStarted;
    //endregion

    private Client currentUser;
    //DUGMICI U DIALOGU
    private SQLiteHelper cashe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_client);

        //cashe init
        cashe = new SQLiteHelper(this);

        //region Views Initialization
        newCar = (Button) findViewById(R.id.NavListButton1);
        clientImage = (ImageView) findViewById(R.id.imageViewNavBarClient);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.headerView);
        navBar = (NavigationView) findViewById(R.id.nav_view);
        signOutBtn = (Button) findViewById(R.id.SignOutBtn);
        FriendsButton = (Button) findViewById(R.id.NavListFriendsButton);
        serviceSwitch = (Switch) findViewById(R.id.serviceSwitch);

        //endregion
        services = new ArrayList<>();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        //dozvola za lokaciju zbog servisa u pozadini
        ActivityCompat.requestPermissions(mainScreenClientActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        //deo sa recycleom
        recycler = (RecyclerView) findViewById((R.id.RecycleViewClient));
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        //Zbog promene lokacije, poziva se value listener cesto, ovo je fleg koji zaustavlja ponovno ucitavanje
        userFetched = false;

        //provera da li je background servis ukljucen ili ne
        backgroundServiceStarted = checkLocationServiceStatus();
        serviceSwitch.setChecked(backgroundServiceStarted);

        adapter = new ItemListClientAdapter(BROJ_PRIKAZANIH_ELEMENATA, this);
        NameAndSurr = (TextView) findViewById(R.id.NameAndSurnameNavBarClient_);
        Descript = (TextView) findViewById(R.id.DescriptionNavBarClient);

        //region OnClick Listeners
        clientImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToUserProfile();

            }
        });

        FriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), FriendsActivity.class));
            }
        });

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putBoolean("locationServiceStatus", b);
                prefEditor.commit();

                if(b)
                {
                    startService(new Intent(getApplicationContext(), LocationService.class));
                    Toast.makeText(mainScreenClientActivity.this,"Location service activated!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    stopService(new Intent(getApplicationContext(), LocationService.class));
                    Toast.makeText(mainScreenClientActivity.this,"Location service deactivated!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //brise podatke o trenutno ulogovanom klijentu prilikom odjavljivanja
                currentClient = null;
                SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                Gson gson = new Gson();
                String json = gson.toJson(currentClient);
                prefEditor.putString("currentClient", json);
                prefEditor.commit();

                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                //alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                alertDialog.setMessage(getString(R.string.areYouSure));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(mainScreenClientActivity.this, getResources().getString(R.string.Signout),
                                        Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                finishAffinity();
                                startActivity(new Intent(getBaseContext(), LoginActivity.class));

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, v.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                stopService(new Intent(getApplicationContext(), LocationService.class));


            }
        });
        //endregion

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client").child(user.getUid());
        mDatabaseReference2 = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService");


        //region dugmad za side meni
        newCar = (Button) findViewById(R.id.NavListButton1);
        testDugme = (Button) findViewById(R.id.NavListButton2);
        newCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), addVehicleFormActivity.class));
            }
        });
        testDugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ServiceRequestActivity.class));
            }
        });
        newCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),addVehicleFormActivity.class));
            }
        });
        mapView = (Button) findViewById(R.id.NavListButton3);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),NewMapActivity.class));
            }
        });
        //endregion

        //region Database event listeners
        mChildEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                VehicleService vehicleService = dataSnapshot.getValue(VehicleService.class);

                adapter.add(vehicleService);

                //za slucaj da se ova lista ucita pre korisnika
                if (currentUser != null)
                    recycler.setAdapter(adapter);
                services.add(vehicleService);

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


        mDatabaseReference2.addChildEventListener(mChildEventListener2);



        //citanje trenutnog klijenta iz baze
        mChildEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!userFetched) {
                    currentUser = dataSnapshot.getValue(Client.class);
                    SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = settings.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(currentUser);
                    prefEditor.putString("currentClient", json);
                    prefEditor.commit();
                    adapter.setUserLocation(new LatLng(currentUser.getLastKnownLat(), currentUser.getLastKnownlongi()));
                    NameAndSurr.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
                    Descript.setText(currentUser.getEmail());
                    recycler.setAdapter(adapter);
                    userFetched = true;
                }

                //startujemo servis ovde jer nam je potreban currentUser, a desava se da se ne ucita dovoljno brzo
                if (currentUser != null && backgroundServiceStarted) {
                    startService(new Intent(getApplicationContext(), LocationService.class));
                    backgroundServiceStarted = false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addValueEventListener(mChildEventListener);

        //pribavljanje slike iz baze ili kesa
        final StorageReference photoRef = mStorageReference.child("photos").child(auth.getCurrentUser().getUid());
        if (!cashe.imageExists(auth.getCurrentUser().getUid())) {
            try {
                final File localFile = File.createTempFile(auth.getCurrentUser().getUid(), "");
                photoRef.getFile(localFile).addOnSuccessListener(this, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        String filePath = localFile.getPath();
                        Bitmap image = BitmapFactory.decodeFile(filePath);
                        cashe.saveImage(auth.getCurrentUser().getUid(), image);
                        if (image != null) {
                            Bitmap profileImage = getRoundedCornerBitmap(Bitmap.createScaledBitmap(image, 250, 250, false), 50);

                            clientImage.setImageBitmap(profileImage);
                        }
                    }
                });

                photoRef.getFile(localFile).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Bitmap image = BitmapFactory.decodeResource(getResources(),
                                R.drawable.sport_car_logos);

                        clientImage.setImageBitmap(image);

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bitmap cashedImage = cashe.getImage(auth.getCurrentUser().getUid());
                Bitmap profileImage = getRoundedCornerBitmap(Bitmap.createScaledBitmap(cashedImage, 250, 250, false), 50);
                clientImage.setImageBitmap(profileImage);
                clientImage.setImageBitmap(cashedImage);
        }

        //endregion

        //servis u backgroundu za lokaciju
        startService(new Intent(this, LocationService.class));

    }
    private void redirectToUserProfile()
    {
        Intent userProfile = new Intent(this, clientInfoActivity.class);
        userProfile.putExtra("editable", true);

        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentUser);
        prefEditor.putString("clientInfo", json);
        prefEditor.commit();

        startActivity(userProfile);
    }



    //Onclick event za klik na neki od servisa onosno neke od slika na ekranu
    @Override
    public void OnItemClick(int clickItemIndex) {
        Intent serviceIntent = new Intent(this, ServiceInfoActivity.class);
        VehicleService temp = services.get(clickItemIndex);


        //udaljenost servisa od korisnika
        float[] results = new float[10];
        Location.distanceBetween(temp.getLat(),temp.getLongi(),currentUser.getLastKnownLat(),currentUser.getLastKnownlongi(),results);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String result = String.valueOf(df.format(results[0]/1000));
        serviceIntent.putExtra("distance",result);
        serviceIntent.putExtra("editable",false);

        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(temp);
        prefEditor.putString("infoService", json);
        prefEditor.commit();

        startActivity(serviceIntent);

    }

    private boolean checkLocationServiceStatus()
    {

        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        return shared.getBoolean("locationServiceStatus",false);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //region roundedImage
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
    //endregion
}
