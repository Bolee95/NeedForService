package ynca.nfs.Activities.mainScreensActivities;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import java.util.HashMap;

import ynca.nfs.Activities.clientActivities.Client_Inbox_Activity;
import ynca.nfs.Activities.clientActivities.addVehicleFormActivity;
import ynca.nfs.Activities.clientActivities.Feedback_activity;
import ynca.nfs.Activities.clientActivities.Info_client;
import ynca.nfs.Activities.clientActivities.Message_activity;
import ynca.nfs.Activities.clientActivities.NewMapActivity;
import ynca.nfs.Activities.startActivities.LoginActivity;
import ynca.nfs.Activities.ZahtevServisiranja;
import ynca.nfs.Adapter.ItemListClientAdapter;
import ynca.nfs.Models.Client;
import ynca.nfs.Models.Poruka;
import ynca.nfs.Models.Review;
import ynca.nfs.R;
import ynca.nfs.Models.VehicleService;

public class mainScreenClientActivity extends AppCompatActivity implements ItemListClientAdapter.OnItemsClickListener {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseDatabase mFirebaseDatabase2;
    private DatabaseReference mDatabaseReference2;
    private ChildEventListener mChildEventListener2;


    private FirebaseDatabase mFirebaseDatabase3;
    private DatabaseReference mDatabaseReference3;
    private ChildEventListener mChildEventListener3;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private static Client trenutniKlijent;
    private ArrayList<VehicleService> servisi;

    private static HashMap<String, Poruka> poruke;
    private static int BROJ_NEPROCITANIH_PORUKA = 0;
    private ArrayList<Float> listaProsecnihOcena ;

    //TODO probaj da resis NavView sa listom <item>
    private TextView DialogServiceName;
    private TextView DialogAdress;
    private TextView DialogEmail;
    private TextView DialogNumber;
    private static final int BROJ_PRIKAZANIH_ELEMENATA = 6;
    private ItemListClientAdapter adapter;
    private RecyclerView recycler;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private NavigationView navBar;
    private TextView test;
    private Button SOSCallClient;
    private TextView NameAndSurr;
    private TextView Descript;

    //Dugmici iz navigacionog bara
    private  Button NovoVozilo;
    private  Button testDugme;
    private Button testMapa;
    private ImageView slikaKlijent;
    private Button InboxBtn;
    private Button NikolaTest;
    private Button signOutBtn;
    private RatingBar rating;



    //DUGMICI U DIALOGU
    private  Button request;
    private Button sendMsg;
    private Button rateComm;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen_client);
        NovoVozilo = (Button) findViewById(R.id.NavListButton1);
        slikaKlijent = (ImageView) findViewById(R.id.imageViewNavBarClient);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.headerView);
        navBar = (NavigationView) findViewById(R.id.nav_view);
       // SOSCallClient = (Button) findViewById(R.id.NavListButton4);
        signOutBtn = (Button) findViewById(R.id.SignOutBtn);
        servisi = new ArrayList<>();
        poruke = new HashMap<>();
        listaProsecnihOcena = new ArrayList<Float>();


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        //test = (TextView) findViewById(R.id.main_menu_header);
        //deo sa recycleom
        recycler = (RecyclerView) findViewById((R.id.RecycleViewClient));
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recycler.setLayoutManager(layoutManager);
        //      layoutManager.addView(test);
        recycler.setHasFixedSize(true);
        adapter = new ItemListClientAdapter(BROJ_PRIKAZANIH_ELEMENATA, this);

        NameAndSurr = (TextView) findViewById(R.id.NameAndSurnameNavBarClient_);
        Descript = (TextView) findViewById(R.id.DescriptionNavBarClient);
        slikaKlijent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Info_client.class));
            }
        });


        signOutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //brise podatke o trenutno ulogovanom klijentu prilikom odjavljivanja
                trenutniKlijent = null;
                SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                Gson gson = new Gson();
                String json = gson.toJson(trenutniKlijent);
                prefEditor.putString("TrenutniKlijent", json);
                prefEditor.commit();

                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle(v.getResources().getString(R.string.warrning));
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


//                Toast.makeText(Main_screen_client.this, getResources().getString(R.string.Signout),
//                        Toast.LENGTH_SHORT).show();


            }
        });



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client");
        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference().child("Korisnik").child("VehicleService");
        mFirebaseDatabase3 = FirebaseDatabase.getInstance();
        mDatabaseReference3 = mFirebaseDatabase3.getReference().child("Korisnik").child("Client")
                .child(user.getUid()).child("primljenePoruke");

        NovoVozilo = (Button) findViewById(R.id.NavListButton1);
        testDugme = (Button) findViewById(R.id.NavListButton2);
        NovoVozilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), addVehicleFormActivity.class));
            }
        });
        testDugme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ZahtevServisiranja.class));
            }
        });
        NovoVozilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),addVehicleFormActivity.class));
            }
        });
        testMapa = (Button) findViewById(R.id.NavListButton3);
        testMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),NewMapActivity.class));
            }
        });
        InboxBtn = (Button) findViewById(R.id.NavListInboxBTN);
        InboxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void  onClick(View v){
                startActivity(new Intent(getBaseContext(), Client_Inbox_Activity.class));
            }
        });


        mChildEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                VehicleService vehicleService = dataSnapshot.getValue(VehicleService.class);

                adapter.add(vehicleService);
                recycler.setAdapter(adapter);
                servisi.add(vehicleService);

                if(vehicleService.getReviews() == null){
                    listaProsecnihOcena.add((float)0);
                    return;
                }
                ArrayList<Review> rec = new ArrayList<>(vehicleService.getReviews().values());
                float fl = 0;
                for(Review r: rec)
                    fl+= r.getRate();

                listaProsecnihOcena.add(fl/((float)rec.size()));



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
        recycler.setAdapter(adapter);


    }
        //Onclick event za klik na neki od servisa onosno neke od slika na ekranu
        @Override
        public void OnItemClick(int clickItemIndex) {
            final VehicleService temp1 = servisi.get(clickItemIndex);
            final float prosecnaOcena = listaProsecnihOcena.get(clickItemIndex);
           Dialog d=new Dialog(mainScreenClientActivity.this);
            d.setContentView(R.layout.dialogbox);
            rating = (RatingBar) d.findViewById(R.id.ratingBar);
            request = (Button) d.findViewById(R.id.ButtonServiceRequest);
            sendMsg = (Button) d.findViewById(R.id.ButtonServiceMessage);
            rateComm = (Button) d.findViewById(R.id.ButtonRateAndComment);
            DialogEmail = (TextView) d.findViewById(R.id.SeriviceName);

            rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingBar.setRating(prosecnaOcena);
                }
            });

            rateComm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getBaseContext(), Feedback_activity.class);
                    i.putExtra("ServisKojiSeOcenjuje", DialogEmail.getText().toString() );

                    startActivity(i);


                }
            });
            DialogAdress = (TextView) d.findViewById(R.id.ServiceAdressResult);
            DialogServiceName = (TextView) d.findViewById(R.id.ServiceNameResult);
            DialogEmail = (TextView) d.findViewById(R.id.ServiceEmailResult);
            DialogNumber = (TextView) d.findViewById(R.id.ServiceNumberResult);


            DialogAdress.setText(String.valueOf(temp1.getAddress()));
            DialogNumber.setText(String.valueOf(temp1.getPhoneNumber()));
            DialogEmail.setText(String.valueOf(temp1.getEmail()));
            DialogServiceName.setText(String.valueOf(temp1.getName()));
            rating.setRating(prosecnaOcena);
            d.setTitle(getResources().getString(R.string.InfoAboutService));
            d.show();

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getBaseContext(), ZahtevServisiranja.class));

                }
            });
            sendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sp = getSharedPreferences("SharedData", MODE_PRIVATE);

                    Intent i = new Intent(getBaseContext(), Message_activity.class);




                    String mailTO = temp1.getEmail();
                    Poruka p = new Poruka();
                    p.setPosiljalac(mailTO);


                    i.putExtra("MSG_DST", p);
                    i.putExtra("isReply", true);




                    startActivity(i);



                }
            });

        }
        public void dialPhoneNumber(String phoneNumber) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }


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
                        mainScreenClientActivity.this.finishAffinity();

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
    protected void onResume(){

        super.onResume();
        final StorageReference photoRef = mStorageReference.child("photos").child(auth.getCurrentUser().getUid());

        photoRef.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    //showProgressDialog();
                    Glide.with(slikaKlijent.getContext())
                            .load(uri).into(slikaKlijent);
                    //hideProgressDialog();
                }
            }
        });

        //citanje trenutnog klijenta iz baze

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Client k = dataSnapshot.getValue(Client.class);
                if(k.getEmail() == null) return;
                if(k.getEmail().equals(user.getEmail())) {
                    trenutniKlijent = k;

                    int broj = 0;
                    if(k.getPrimljenePoruke() != null) {
                        ArrayList<Poruka> li = new ArrayList<Poruka>(k.getPrimljenePoruke().values());
                        for (Poruka p : li) {
                            if (!p.isProcitana())
                                broj++;
                        }
                        BROJ_NEPROCITANIH_PORUKA = broj;

                        if (BROJ_NEPROCITANIH_PORUKA > 0) {
                            InboxBtn.setText(getResources().getString(R.string.NavListInboxBtn) + "(" + Integer.toString(BROJ_NEPROCITANIH_PORUKA) + ")");
                        } else
                            InboxBtn.setText(getResources().getString(R.string.NavListInboxBtn));
                    } else {
                        InboxBtn.setText(getResources().getString(R.string.NavListInboxBtn));
                    }

                    int br;
                    if(trenutniKlijent.getPrimljenePoruke() == null){
                        br =0;
                    }
                    else{
                        br = trenutniKlijent.getPrimljenePoruke().size();
                    }

                    SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = settings.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(trenutniKlijent);
                    prefEditor.putInt("brojPoruka", br);
                    prefEditor.putString("TrenutniKlijent", json);
                    //prefEditor.putInt("brojNeprocitanih", BROJ_NEPROCITANIH_PORUKA);
                    prefEditor.commit();
                    NameAndSurr.setText(trenutniKlijent.getFirstName() + " " + trenutniKlijent.getLastName());
                    Descript.setText(trenutniKlijent.getEmail());
                }


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


        mChildEventListener3 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Poruka p = dataSnapshot.getValue(Poruka.class);
//                if(!p.isProcitana())
//                    BROJ_NEPROCITANIH_PORUKA++;

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

        mDatabaseReference3.addChildEventListener(mChildEventListener3);
    }
    public static void resetujBrojNeprocitanihPoruka()
    {
        //BROJ_NEPROCITANIH_PORUKA =0;
    }
    public static  void dekrementirajBrojNeprocitanihPoruka()
    {
        //BROJ_NEPROCITANIH_PORUKA--;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //BROJ_NEPROCITANIH_PORUKA = 0;
    }
}








