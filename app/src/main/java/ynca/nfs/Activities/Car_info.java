package ynca.nfs.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import ynca.nfs.Models.Automobil;
import ynca.nfs.Models.Klijent;
import ynca.nfs.R;

public class Car_info extends AppCompatActivity {
    private TextView Manufact;
    private TextView Model;
    private TextView Reg;
    private TextView Chass;
    private TextView Fuel;
    private TextView ProdYear;
    private TextView Milage;
    private TextView LastService;
    private Button Delete;
    Automobil automobil;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);
        String temp = getIntent().getStringExtra("Registarski");


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));

        Delete = (Button) findViewById(R.id.ButtonListCars);
        Manufact = (TextView) findViewById(R.id.ManufacturerCarResult);
        Model = (TextView) findViewById(R.id.ModelCarResult);
        Reg = (TextView) findViewById(R.id.RegistyNumCarResult);
        Chass = (TextView) findViewById(R.id.chassisNumCarResult);
        Fuel = (TextView) findViewById(R.id.FuelTypeCarResult);
        ProdYear = (TextView) findViewById(R.id.YearCarResult);
        Milage = (TextView) findViewById(R.id.MileageCarResult);
        LastService = (TextView) findViewById(R.id.lastServicedCarResult);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        automobil = (Automobil) getIntent().getSerializableExtra("vozilo");

        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = settings.getString("TrenutniKlijent","");
        Klijent obj = gson.fromJson(json, Klijent.class);
        if (obj == null)
        {Delete.setVisibility(View.INVISIBLE);}

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle(v.getResources().getString(R.string.warrning));
                alertDialog.setMessage(getString(R.string.areYouSure));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, v.getResources().getString(R.string.Yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO ODRADI LOGIKU DA IZBRISE IZ LISTE I DA POSALJE PORUKU KORISNIKU
                                    mDatabaseReference.child("Korisnik").child("Klijent").child(mUser.getUid())
                                            .child("listaVozila").child(automobil.getVoziloID()).removeValue();
                                    Toast.makeText(Car_info.this, R.string.succeed, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Car_info.this, ListaVozilaActivity.class));

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, v.getResources().getString(R.string.No) , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

//                mDatabaseReference.child("Korisnik").child("Klijent").child(mUser.getUid())
//                        .child("listaVozila").child(automobil.getVoziloID()).removeValue();
//                Toast.makeText(Car_info.this, R.string.succeed, Toast.LENGTH_LONG).show();
//                startActivity(new Intent(Car_info.this, ListaVozilaActivity.class));
            }
        });

        if(temp != null)
        {
            Iterator it = obj.getListaVozila().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Object t =  pair.getKey();
                Automobil x = obj.getListaVozila().get(t);
                if (x.getRegBroj().equals(temp))
                {
                    Manufact.setText(x.getProizvodjac());
                    Model.setText(x.getModel());
                    Reg.setText(String.valueOf(x.getRegBroj()));
                    Chass.setText(String.valueOf(x.getBrojSasije()));
                    Fuel.setText(x.getTipGoriva());
                    ProdYear.setText(String.valueOf(x.getGodinaProizvodnje()));
                    Milage.setText(String.valueOf(x.getPredjeniPut()));
                    LastService.setText(String.valueOf(x.getPoslednjiServisDatum()));
                    continue;

                }
                //iskoristi continue;
                it.remove(); // avoids a ConcurrentModificationException
            }
        }



    }
}