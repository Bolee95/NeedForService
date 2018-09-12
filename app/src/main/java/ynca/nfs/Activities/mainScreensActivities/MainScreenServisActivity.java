package ynca.nfs.Activities.mainScreensActivities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

import ynca.nfs.Activities.ListaCenovnikUslugaActivity;
import ynca.nfs.Activities.ListaZahtevaActivity;
import ynca.nfs.Activities.Lista_Recenzija_Activity;
import ynca.nfs.Activities.ServiceInfoActivity;
import ynca.nfs.Activities.startActivities.LoginActivity;
import ynca.nfs.Adapter.ListaVozilaNaServisuAdapter;
import ynca.nfs.Models.Vehicle;
import ynca.nfs.R;
import ynca.nfs.Models.VehicleService;

public class MainScreenServisActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase2;
    private DatabaseReference mDatabaseReference2;
    private ChildEventListener mChildEventListener2;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private StorageReference mStorageReference;
    private static VehicleService currentService;

    private ListaVozilaNaServisuAdapter theAdapter;
    private RecyclerView theRecyclerView;
    private ArrayList<Vehicle> listOfVehicles;

    Button priceOfServiceButton;
    Button requestsButton;
    Button reviewsButton;
    private Button signOutBtn;
    private TextView NameOfService;
    private TextView Descript;
    private ImageView serviceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_servis);

        NameOfService = (TextView) findViewById(R.id.NameAndSurnameNavBarServis);
        Descript = (TextView) findViewById(R.id.DescriptionNavBarServis);
        priceOfServiceButton = (Button) findViewById(R.id.cenovnik_usluga_id);
        requestsButton = (Button) findViewById(R.id.zahtevi_id);
        signOutBtn = (Button) findViewById(R.id.SignOutBtn);
        serviceImage = (ImageView) findViewById(R.id.imageViewNavBarServis);
        reviewsButton = (Button) findViewById(R.id.lista_recenzija_btn_id);
        listOfVehicles = new ArrayList<>();


        priceOfServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getBaseContext(), ListaCenovnikUslugaActivity.class);
                startActivity(new Intent(intent));
            }
        });

//        mVozilaNaServisu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getBaseContext(), ListaVozilaNaServisuActivity.class));
//            }
//        });

        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ListaZahtevaActivity.class));
            }
        });


        serviceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ServiceInfoActivity.class));
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();


                //brise podatke o trenutno ulogovanom servisu prilikom logout-a
                currentService = null;
                SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                Gson gson = new Gson();
                String json = gson.toJson(currentService);
                prefEditor.putString("TrenutniServis", json);
                prefEditor.commit();


                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                alertDialog.setMessage(getString(R.string.areYouSure));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(MainScreenServisActivity.this,  getResources().getString(R.string.Signout),
                                        Toast.LENGTH_SHORT).show();

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


//                FirebaseAuth.getInstance().signOut();
//                Toast.makeText(MainScreenServisActivity.this,  getResources().getString(R.string.Signout),
//                        Toast.LENGTH_SHORT).show();
//
//                //brise podatke o trenutno ulogovanom servisu prilikom logout-a
//                currentService = null;
//                SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
//                SharedPreferences.Editor prefEditor = settings.edit();
//                Gson gson = new Gson();
//                String json = gson.toJson(currentService);
//                prefEditor.putString("TrenutniServis", json);
//                prefEditor.commit();



            }
        });

        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Lista_Recenzija_Activity.class));
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        theAdapter = new ListaVozilaNaServisuAdapter(new ListaVozilaNaServisuAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {

            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService");

        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference().child("Korisnik").child("VehicleService")
                .child(user.getUid()).child("acceptedServices");


        theRecyclerView = (RecyclerView) findViewById(R.id.lista_vozila_na_servisu_recycle_view_id2);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        theRecyclerView.setLayoutManager(llm);
        theRecyclerView.setHasFixedSize(true);




        mChildEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Vehicle a2 = dataSnapshot.getValue(Vehicle.class);
                a2.setVehicleID(dataSnapshot.getKey());
                mDatabaseReference2.child(a2.getVehicleID()).setValue(a2);
                theAdapter.add(a2);
                theRecyclerView.setAdapter(theAdapter);
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

    }



    @Override
    public void onBackPressed() {

        //super.onBackPressed();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getBaseContext().getResources().getString(R.string.warrning));
        alertDialog.setMessage(getString(R.string.areYouSure));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getBaseContext().getResources().getString(R.string.Yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //this.finishAffinity();
                        MainScreenServisActivity.this.finishAffinity();

                        //System.exit(1);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getBaseContext().getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //proverava da li vec postoji slika servisa
        StorageReference photoRef = mStorageReference.child("photos").child(auth.getCurrentUser().getUid());
        photoRef.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    //showProgressDialog();
                    Glide.with(serviceImage.getContext())
                            .load(uri).into(serviceImage);
                    //hideProgressDialog();
                }
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                VehicleService ser = dataSnapshot.getValue(VehicleService.class);
                if(ser.getEmail() == null) return;
                if(ser.getEmail().equals(user.getEmail())) {
                    currentService = ser;
                }

                    int br;
                    if(currentService.getPrimljenePoruke() == null){
                        br =0;
                    }
                    else{
                        br = currentService.getPrimljenePoruke().size();
                    }

                    SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = settings.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(currentService);
                    prefEditor.putInt("brojPoruka", br);
                    prefEditor.putString("TrenutniServis", json);
                    prefEditor.commit();
                    NameOfService.setText(currentService.getName() );
                    Descript.setText(currentService.getEmail());

                }
//                Automobil a2 = dataSnapshot.getValue(Automobil.class);
//                theAdapter.add(a2);
//                theRecyclerView.setAdapter(theAdapter);
//                listOfVehicles.add(a2);


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
}
























