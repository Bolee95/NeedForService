package ynca.nfs.Activities.clientActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.Map;

import ynca.nfs.Models.Client;
import ynca.nfs.Models.Vehicle;
import ynca.nfs.R;

public class carInfoActivity extends AppCompatActivity {
    private TextView Manufact;
    private TextView Model;
    private TextView Reg;
    private TextView Chass;
    private TextView Fuel;
    private TextView ProdYear;
    private TextView Milage;
    private TextView LastService;
    private Button Delete;
    Vehicle vehicle;

    private Toolbar toolbar;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        String temp = getIntent().getStringExtra("Registarski");

        Delete = (Button) findViewById(R.id.ButtonListCars);
        Manufact = (TextView) findViewById(R.id.ManufacturerCarResult);
        Model = (TextView) findViewById(R.id.ModelCarResult);
        Reg = (TextView) findViewById(R.id.RegistyNumCarResult);
        Chass = (TextView) findViewById(R.id.chassisNumCarResult);
        Fuel = (TextView) findViewById(R.id.FuelTypeCarResult);
        ProdYear = (TextView) findViewById(R.id.YearCarResult);
        Milage = (TextView) findViewById(R.id.MileageCarResult);
        LastService = (TextView) findViewById(R.id.lastServicedCarResult);


        //Toolbar podesavanja
        toolbar = (Toolbar) findViewById(R.id.carInfoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_18dp);
        getSupportActionBar().setTitle("");


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        vehicle = (Vehicle) getIntent().getSerializableExtra("vozilo");

        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = settings.getString("currentClient","");
        Client obj = gson.fromJson(json, Client.class);
        //if (obj == null)
        //{Delete.setVisibility(View.INVISIBLE);}

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                //alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                alertDialog.setMessage(getString(R.string.areYouSure2));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                    mDatabaseReference.child("Korisnik").child("Client").child(mUser.getUid())
                                            .child("listaVozila").child(vehicle.getVehicleID()).removeValue();
                                    Toast.makeText(carInfoActivity.this, R.string.succeed, Toast.LENGTH_LONG).show();
                                    finish();

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, v.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });


        Manufact.setText(vehicle.getManufacturer());
        Model.setText(vehicle.getModel());
        Reg.setText(String.valueOf(vehicle.getRegistyNumber()));
        Chass.setText(String.valueOf(vehicle.getChassisNumber()));
        Fuel.setText(vehicle.getFuelType());
        ProdYear.setText(String.valueOf(vehicle.getYearOfProduction()));
        Milage.setText(String.valueOf(vehicle.getMileage()));

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
