package ynca.nfs.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ynca.nfs.Models.Request;
import ynca.nfs.Models.Vehicle;
import ynca.nfs.R;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.Models.Job;


/**
 * Created by Nikola on 5/22/2017.
 */

public class ZahtevServisiranja extends AppCompatActivity {

    EditText mTypeOfService;
    EditText mProposedDates;
    EditText mNote;

    Button mButtonSend;

    Spinner mUsluge;
    Spinner mAutomobili;
    Spinner mServisi;

    ArrayList<Vehicle> mAutomobiliLista;
    ArrayList<VehicleService> mServisiLista;
    ArrayList<Job> usluge;
    List<String> imenaUsluga;
    List<String> listServisa;
    List<String> listAutomobila;

    static Vehicle selectedAuto = null;
    static VehicleService selectedVehicleService = null;
    static Job selectedUsluga = null;

    ArrayAdapter<String> adapterUsluga;

    private DatabaseReference mDatabaseReference3;
    private FirebaseDatabase mFirebaseDatabase3;
    private ChildEventListener mChildEventListener3;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseDatabase mFirebaseDatabase1;
    private FirebaseDatabase mFirebaseDatabase2;
    private DatabaseReference mDatabaseReference1;
    private DatabaseReference mDatabaseReference2;
    private ChildEventListener mChildEventListener1;
    private ChildEventListener mChildEventListener2;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zahtev_za_servisiranje_form);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        //mTypeOfService = (EditText) findViewById(R.id.type_of_service_id);
        mProposedDates = (EditText) findViewById(R.id.proposed_dates_id);
        mNote = (EditText) findViewById(R.id.note_id);

        mAutomobili = (Spinner) findViewById(R.id.automobili_klijenta_id);
        mServisi = (Spinner) findViewById(R.id.servisi_dostupni_klijentu_spinner);
        mButtonSend = (Button) findViewById(R.id.posalji_zahtev_btn_id);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUsluge = (Spinner) findViewById(R.id.usluge_servis_spinner);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mFirebaseDatabase1 = FirebaseDatabase.getInstance();
        mDatabaseReference1 = mFirebaseDatabase1.getReference().child("Korisnik")
                .child("Client").child(mUser.getUid())
                .child("listOfCars");
        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference().child("Korisnik").child("VehicleService");

        mServisiLista = new ArrayList<>();
        mAutomobiliLista = new ArrayList<>();
        listServisa = new ArrayList<String>();
        listAutomobila = new ArrayList<String>();
        imenaUsluga = new ArrayList<>();
        usluge = new ArrayList<>();


        final ArrayAdapter<String> adapterAuto = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listAutomobila);
        final ArrayAdapter<String> adapterServis = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listServisa);
        adapterUsluga = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, imenaUsluga);


        mAutomobili.setAdapter(adapterAuto);
        mServisi.setAdapter(adapterServis);
        mUsluge.setAdapter(adapterUsluga);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedVehicleService == null || selectedUsluga == null || selectedAuto == null)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.ZahtevFail) , Toast.LENGTH_LONG).show();
                    return;

                }
                String date = mProposedDates.getText().toString();
                String note = mNote.getText().toString();
                Request z = new Request(selectedUsluga.getJob(), date, note, selectedAuto, selectedVehicleService.getUID() , mUser.getUid(), mUser.getEmail());
                //mDatabaseReference.child("Korisnik").child("Client").child(mUser.getUid()).child("zahtevi").push().setValue(z);
                //mDatabaseReference.child("Korisnik").child("VehicleService").child(z.getServis().getUID()).child("zahtevi").push().setValue(z);
                mDatabaseReference.child("ServiceRequests").child(selectedVehicleService.getUID()).push().setValue(z);
                //mDatabaseReference.child("ZahteviKlijent").child(mUser.getUid()).push().setValue(z);
        //        mDatabaseReference.push().setValue(z);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.ZahtevPoslat), Toast.LENGTH_LONG).show();
                finish();


            }
        });

        mUsluge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUsluga = usluge.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mAutomobili.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAuto = mAutomobiliLista.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mServisi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicleService = mServisiLista.get(position);
                int i = 0;
                if(selectedVehicleService.getServices() == null) {
                    imenaUsluga.clear();
                    adapterUsluga.notifyDataSetChanged();
                    return;
                }
                usluge = new ArrayList<Job>(selectedVehicleService.getServices().values());
                imenaUsluga.clear();
                for(Job u: usluge){
                    imenaUsluga.add(u.getJob() + " " + u.getPrice() + " RSD");
                }
                adapterUsluga.notifyDataSetChanged();
                mUsluge.setAdapter(adapterUsluga);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        mChildEventListener1 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Vehicle a = dataSnapshot.getValue(Vehicle.class);
                mAutomobiliLista.add(a);
                listAutomobila.add(a.getManufacturer() + " " +a.getModel());
                adapterAuto.notifyDataSetChanged();
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

        mDatabaseReference1.addChildEventListener(mChildEventListener1);




        mChildEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                VehicleService vehicleService =dataSnapshot.getValue(VehicleService.class);
                mServisiLista.add(vehicleService);
                //if(vehicleService.getListaUsluga()!=null){
                //usluge = vehicleService.getListaUsluga();
                //Collection<Usluga> str  = usluge.values();}
                listServisa.add(vehicleService.getName());
                adapterServis.notifyDataSetChanged();
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
}
