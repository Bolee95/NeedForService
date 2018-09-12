package ynca.nfs.Activities.clientActivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ynca.nfs.Models.Vehicle;
import ynca.nfs.R;

public class addVehicleFormActivity extends AppCompatActivity {

    EditText manufacturer;
    EditText model;
    EditText registryNumber;
    EditText sassiesNumber;
    EditText fuelType;
    EditText yearOfProduction;
    EditText mileage;
    //EditText lastService;

    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Toolbar toolbar;

    Button submitButton;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.dodaj_automobil_form);

        manufacturer = (EditText) findViewById(R.id.proizvodjacEDIT);
        model = (EditText) findViewById(R.id.modelEDIT);
        registryNumber = (EditText) findViewById(R.id.regBrEDIT);
        sassiesNumber = (EditText) findViewById(R.id.BrSasijeEDIT);
        fuelType = (EditText) findViewById(R.id.tipGorivaEDIT);
        yearOfProduction = (EditText) findViewById(R.id.godinaProizvodnjeEDIT);
        mileage = (EditText) findViewById(R.id.predjeniKmEDIT);
        //lastService = (EditText) findViewById(R.id.servisiranEDIT);


        //Toolbar podesavanja
        toolbar = (Toolbar) findViewById(R.id.newCarToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_18dp);
        getSupportActionBar().setTitle(R.string.newVehicle);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        submitButton = (Button) findViewById(R.id.DodajAutomobilBTN);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (model.getText().toString().equals("") || manufacturer.getText().toString().equals(" ") || mileage.getText().toString().equals(" ")
                        || sassiesNumber.getText().toString().equals(" ") || fuelType.getText().toString().equals(" ") || yearOfProduction.getText().toString().equals(" ") ) {

                    AlertDialog alertDialog = new AlertDialog.Builder(addVehicleFormActivity.this).create();
                    alertDialog.setTitle(getResources().getString(R.string.Warning));
                    alertDialog.setMessage(getResources().getString(R.string.EmptyFields));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();




                }
                else {
                    Vehicle a = new Vehicle(registryNumber.getText().toString(),
                            model.getText().toString(),
                            manufacturer.getText().toString(),
                            Integer.valueOf(mileage.getText().toString()),
                            Integer.valueOf(sassiesNumber.getText().toString()),
                            fuelType.getText().toString(),
                            Integer.valueOf(yearOfProduction.getText().toString()),
                            mUser.getUid(),
                            mUser.getEmail(),
                            "");

                    mDatabaseReference.child("Korisnik").child("Client").child(mUser.getUid()).child("listOfCars").push().setValue(a);
                    Toast.makeText(getApplicationContext(), R.string.Added_new_car, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

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
