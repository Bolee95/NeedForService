package ynca.nfs.Activities;

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ynca.nfs.Automobil;
import ynca.nfs.R;

/**
 * Created by Nikola on 5/22/2017.
 */

public class DodajAutomobilForm extends AppCompatActivity {

    EditText proizvodjacET;
    EditText modelET;
    EditText registarskiBrojET;
    EditText brojSasijeET;
    EditText tipGorivaET;
    EditText godinaProizvodnjeET;
    EditText kilometrET;
    EditText poslednjiServisET;

    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Button dodajBtn;

    //treba nam korisnik

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.dodaj_automobil_form);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        proizvodjacET = (EditText) findViewById(R.id.proizvodjacEDIT);
        modelET = (EditText) findViewById(R.id.modelEDIT);
        registarskiBrojET = (EditText) findViewById(R.id.regBrEDIT);
        brojSasijeET = (EditText) findViewById(R.id.BrSasijeEDIT);
        tipGorivaET = (EditText) findViewById(R.id.tipGorivaEDIT);
        godinaProizvodnjeET = (EditText) findViewById(R.id.godinaProizvodnjeEDIT);
        kilometrET = (EditText) findViewById(R.id.predjeniKmEDIT);
        poslednjiServisET = (EditText) findViewById(R.id.servisiranEDIT);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        dodajBtn = (Button) findViewById(R.id.DodajAutomobilBTN);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //Toast.makeText(getApplicationContext(), mUser.getProviderId(), Toast.LENGTH_LONG).show();
        //String s = mUser.getUid();

        dodajBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (modelET.getText().toString().equals("") || proizvodjacET.getText().toString().equals(" ") || kilometrET.getText().toString().equals(" ")
                        || brojSasijeET.getText().toString().equals(" ") || tipGorivaET.getText().toString().equals(" ") || godinaProizvodnjeET.getText().toString().equals(" ") || poslednjiServisET.getText().toString().equals(" ")) {

                    AlertDialog alertDialog = new AlertDialog.Builder(DodajAutomobilForm.this).create();
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
                    Automobil a = new Automobil(registarskiBrojET.getText().toString(),
                            modelET.getText().toString(),
                            proizvodjacET.getText().toString(),
                            Integer.valueOf(kilometrET.getText().toString()),
                            Integer.valueOf(brojSasijeET.getText().toString()),
                            tipGorivaET.getText().toString(),
                            Integer.valueOf(godinaProizvodnjeET.getText().toString()),
                            poslednjiServisET.getText().toString(),
                            mUser.getUid(),
                            mUser.getEmail(),
                            "");

                    mDatabaseReference.child("Korisnik").child("Klijent").child(mUser.getUid()).child("listaVozila").push().setValue(a);
                    Toast.makeText(getApplicationContext(), R.string.Added_new_car, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

    }




}
