package ynca.nfs.Activities.clientActivities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ynca.nfs.Models.Request;
import ynca.nfs.Models.Vehicle;
import ynca.nfs.R;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.Models.Job;


/**
 * Created by Nikola on 5/22/2017.
 */

public class ServiceRequestActivity extends AppCompatActivity {

    EditText mTypeOfService;
    EditText mProposedDates;
    EditText mNote;
    EditText mProposedTime;

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
    private Calendar calendar;
    private TimePicker clock;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;

    private Intent extraIntent;
    private String defaultServiceUid;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zahtev_za_servisiranje_form);

        calendar = Calendar.getInstance();

        //Toolbar podesavanja
        toolbar = (Toolbar) findViewById(R.id.requestToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_18dp);
        getSupportActionBar().setTitle(R.string.serviceRequest);


        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        mProposedDates = (EditText) findViewById(R.id.proposed_dates_id);
        mProposedTime = (EditText) findViewById(R.id.proposed_time_id);
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


        extraIntent = getIntent();

        defaultServiceUid = extraIntent.getStringExtra("serviceUid");


        final ArrayAdapter<String> adapterAuto = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listAutomobila);
        final ArrayAdapter<String> adapterServis = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, listServisa);
        adapterUsluga = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, imenaUsluga);


        mAutomobili.setAdapter(adapterAuto);
        mServisi.setAdapter(adapterServis);
        mUsluge.setAdapter(adapterUsluga);
        //region event listeners
        mProposedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(ServiceRequestActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                mProposedTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        mProposedDates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(ServiceRequestActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


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
                z.setProposedTime(mProposedTime.toString());
                mDatabaseReference.child("ServiceRequests").child(selectedVehicleService.getUID()).push().setValue(z);
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
                if (vehicleService.getUID().equals(defaultServiceUid))
                {
                    mServisi.setSelection(mServisiLista.size() - 1);
                }
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

        //endregion


    }


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        mProposedDates.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }
}
