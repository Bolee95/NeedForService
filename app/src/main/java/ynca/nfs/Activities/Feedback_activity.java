package ynca.nfs.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ynca.nfs.R;
import ynca.nfs.Models.Recenzija;
import ynca.nfs.Models.Servis;

public class Feedback_activity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    static  String mejlServisaKojiSeOcenjuje;

    EditText kom;
    RatingBar rejt;
    Button submit;

    Servis servisZaSlanje;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_activity);
        //Moze preko shered preferences da se prenese identifikator servisa na
        // koji je kliknuto i kasnije da to bude kljuc u tabeli sa komentarom i ocenom

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik")
            .child("Servis");

        mejlServisaKojiSeOcenjuje = getIntent().getStringExtra("ServisKojiSeOcenjuje");

        kom = (EditText) findViewById(R.id.recenzijaEDIT);
        rejt =  (RatingBar) findViewById(R.id.ratingBar);
        submit =  (Button) findViewById(R.id.recenzijaSUBMIT);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  komentar = kom.getText().toString();
                float ocena = rejt.getRating();
                 // TODO Rexenzija = new Recenzija(...) pa u bazu
                Recenzija recenzijaZaPamcenje = new Recenzija(mUser.getEmail(), komentar, ocena);
                mDatabaseReference.child(servisZaSlanje.getUID()).child("recenzije")
                        .push().setValue(recenzijaZaPamcenje);
                Toast.makeText(Feedback_activity.this, "Succeed!", Toast.LENGTH_SHORT);
                finish();
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Servis ser = dataSnapshot.getValue(Servis.class);
                if(ser.getEmail().equals(mejlServisaKojiSeOcenjuje))
                {
                    servisZaSlanje = ser;


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
