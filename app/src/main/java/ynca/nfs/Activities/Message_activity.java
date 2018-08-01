package ynca.nfs.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

import ynca.nfs.Models.Klijent;
import ynca.nfs.Models.Poruka;
import ynca.nfs.R;
import ynca.nfs.Models.Servis;

public class Message_activity extends AppCompatActivity {

    private Activity ovajAktiviti = this;

    private static int BROJ_PORUKA = 0;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference1;
    private FirebaseDatabase mFirebaseDatabase1;
    private ChildEventListener mChildEventListener1;
    private DatabaseReference mDatabaseReference2;
    private FirebaseDatabase mFirebaseDatabase2;
    private ChildEventListener mChildEventListener2;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    ArrayList<Servis> servisi;
    ArrayList<Klijent> klijenti;


    Intent i;
    Poruka poruka;
    EditText destTV;
    Button submit;
    EditText naslovTV;
    EditText tekstTV;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_activity);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));



        destTV = (EditText) findViewById(R.id.destEdit);
        naslovTV = (EditText) findViewById(R.id.naslovEdit);
        tekstTV = (EditText) findViewById(R.id.tekstTV);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        i = getIntent();

        poruka = (Poruka) i.getSerializableExtra("MSG_DST");
        BROJ_PORUKA = i.getIntExtra("BROJ_PORUKA", -1);
        if(i.getBooleanExtra("isReply", false)) //proverava da li je otoviro activity preko reply-a
        {

            destTV.setText(poruka.getPosiljalac());
            destTV.setEnabled(false);
        }

        destTV.setEnabled(false);
        submit = (Button) findViewById(R.id.Message_activity_submit);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference().child("Korisnik")
                .child("Klijent");
        mFirebaseDatabase1 = FirebaseDatabase.getInstance();
        mDatabaseReference1 = mFirebaseDatabase1.getReference().child("Korisnik").child("Servis");


        servisi = new ArrayList<>();
        klijenti = new ArrayList<>();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String korisnkik;
                SharedPreferences sp = getSharedPreferences("SharedData", MODE_PRIVATE);

                Gson gson = new Gson();
                String json = sp.getString("TrenutniKlijent", "");
                Klijent k = gson.fromJson(json, Klijent.class);
                json = sp.getString("TrenutniServis", "");
                Servis s  = gson.fromJson(json, Servis.class );

                String email = destTV.getText().toString();
                String title = naslovTV.getText().toString();
                String tekst = tekstTV.getText().toString();
                Poruka zaSlanje;

                if (k!=null) {

                    zaSlanje = new Poruka(false, k.getEmail(), email, title, tekst,"");
                    mDatabaseReference.child("Korisnik").child("Servis").child(getUIDservisa(email)).child("primljenePoruke").push().setValue(zaSlanje);
                    startActivity(new Intent(getBaseContext(),Client_Inbox_Activity.class));
                    String PorukaJePoslata = getString(R.string.PorukaJePoslata);
                    Toast.makeText(getApplicationContext(), PorukaJePoslata , Toast.LENGTH_LONG).show();
                }
                else {

                    zaSlanje = new Poruka(false, s.getEmail(), email, title, tekst, "");
                    mDatabaseReference.child("Korisnik").child("Klijent").child(getUIDklijenta(email)).child("primljenePoruke").push().setValue(zaSlanje);
                    startActivity(new Intent(getBaseContext(),Servis_Inbox_Activity.class));
                    String PorukaJePoslata = getString(R.string.PorukaJePoslata);
                    Toast.makeText(getApplicationContext(), PorukaJePoslata , Toast.LENGTH_LONG).show();
                }

                ovajAktiviti.finish();

                if (k!=null)
                {

                    startActivity(new Intent(getBaseContext(), Client_Inbox_Activity.class ));


                }
                else
                {
                    startActivity(new Intent(getBaseContext(), Servis_Inbox_Activity.class));
                }

            }
        });


        mChildEventListener1 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Servis servis = dataSnapshot.getValue(Servis.class);
                servisi.add(servis);
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

                Klijent k = dataSnapshot.getValue(Klijent.class);
                klijenti.add(k);
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
        super.onBackPressed();

        startActivity(new Intent(Message_activity.this, Poruka_tekst_activity.class));
    }

    private String getUIDservisa(String email) {
        for(Servis s: servisi){

            if(s.getEmail().equals(email))
                return  s.getUID();

        }
        return null;
    }

    private String getUIDklijenta(String email){
        for(Klijent k: klijenti){

            if(k.getEmail().equals(email))
                return  k.getUID();

        }
        return null;
    }

    private Servis nadjiDest(String email) {
        for(Servis s: servisi){

            if(s.getEmail().equals(email))
                return  s;

        }
        return null;
    }
    private Klijent nadjiDest2(String email) {
        for(Klijent k: klijenti){

            if(k.getEmail().equals(email))
                return  k;

        }
        return null;
    }

}
