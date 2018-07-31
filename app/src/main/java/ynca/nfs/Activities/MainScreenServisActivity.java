package ynca.nfs.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import ynca.nfs.Activities.StartActivities.LoginActivity;
import ynca.nfs.Adapter.ListaVozilaNaServisuAdapter;
import ynca.nfs.Automobil;
import ynca.nfs.Poruka;
import ynca.nfs.R;
import ynca.nfs.Servis;

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
    private static Servis trenutniServis;

    private ListaVozilaNaServisuAdapter theAdapter;
    private RecyclerView theRecyclerView;
    private ArrayList<Automobil> lista;




    Button mCenovnikUsluga;
    //Button mVozilaNaServisu;
    Button mZahtevi;
    Button mSanduce;
    Button mRecenzije;
    private Button signOutBtn;
    private TextView NameOfService;
    private TextView Descript;
    private ImageView slikaServisa;
    private static int BROJ_NEPROCITANIH_PORUKA;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_servis);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        NameOfService = (TextView) findViewById(R.id.NameAndSurnameNavBarServis);
        Descript = (TextView) findViewById(R.id.DescriptionNavBarServis);
        mCenovnikUsluga = (Button) findViewById(R.id.cenovnik_usluga_id);
        mZahtevi = (Button) findViewById(R.id.zahtevi_id);
        mSanduce = (Button) findViewById(R.id.sanduce_servis_id);
        signOutBtn = (Button) findViewById(R.id.SignOutBtn);
        slikaServisa = (ImageView) findViewById(R.id.imageViewNavBarServis);
        mRecenzije = (Button) findViewById(R.id.lista_recenzija_btn_id);
        lista = new ArrayList<>();


        mCenovnikUsluga.setOnClickListener(new View.OnClickListener() {
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

        mZahtevi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ListaZahtevaActivity.class));
            }
        });

        mSanduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Servis_Inbox_Activity.class));
            }
        });

        slikaServisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Info_Servis.class));
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();


                //brise podatke o trenutno ulogovanom servisu prilikom logout-a
                trenutniServis = null;
                SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                Gson gson = new Gson();
                String json = gson.toJson(trenutniServis);
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
//                trenutniServis = null;
//                SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
//                SharedPreferences.Editor prefEditor = settings.edit();
//                Gson gson = new Gson();
//                String json = gson.toJson(trenutniServis);
//                prefEditor.putString("TrenutniServis", json);
//                prefEditor.commit();



            }
        });

        mRecenzije.setOnClickListener(new View.OnClickListener() {
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
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Servis");

        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference().child("Korisnik").child("Servis")
                .child(user.getUid()).child("automobili");


        theRecyclerView = (RecyclerView) findViewById(R.id.lista_vozila_na_servisu_recycle_view_id2);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        theRecyclerView.setLayoutManager(llm);
        theRecyclerView.setHasFixedSize(true);




        mChildEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Automobil a2 = dataSnapshot.getValue(Automobil.class);
                a2.setVoziloID(dataSnapshot.getKey());
                mDatabaseReference2.child(a2.getVoziloID()).setValue(a2);
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
                    Glide.with(slikaServisa.getContext())
                            .load(uri).into(slikaServisa);
                    //hideProgressDialog();
                }
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Servis ser = dataSnapshot.getValue(Servis.class);
                if(ser.getEmail() == null) return;
                if(ser.getEmail().equals(user.getEmail()))
                {
                    trenutniServis = ser;

                    int broj = 0;

                    if(ser.getPrimljenePoruke() != null) {
                        ArrayList<Poruka> li = new ArrayList<Poruka>(trenutniServis.getPrimljenePoruke().values());
                        for (Poruka p : li) {
                            if (!p.isProcitana())
                                broj++;
                        }
                        BROJ_NEPROCITANIH_PORUKA = broj;

                        if (BROJ_NEPROCITANIH_PORUKA > 0) {
                            mSanduce.setText(getResources().getString(R.string.NavListInboxBtn) + "(" + Integer.toString(BROJ_NEPROCITANIH_PORUKA) + ")");
                        } else
                            mSanduce.setText(getResources().getString(R.string.NavListInboxBtn));
                    } else {
                        mSanduce.setText(getResources().getString(R.string.NavListInboxBtn));
                    }

                    int br;
                    if(trenutniServis.getPrimljenePoruke() == null){
                        br =0;
                    }
                    else{
                        br = trenutniServis.getPrimljenePoruke().size();
                    }

                    SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = settings.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(trenutniServis);
                    prefEditor.putInt("brojPoruka", br);
                    prefEditor.putString("TrenutniServis", json);
                    prefEditor.commit();
                    NameOfService.setText(trenutniServis.getNaziv() );
                    Descript.setText(trenutniServis.getEmail());

                }
//                Automobil a2 = dataSnapshot.getValue(Automobil.class);
//                theAdapter.add(a2);
//                theRecyclerView.setAdapter(theAdapter);
//                lista.add(a2);
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
}
























