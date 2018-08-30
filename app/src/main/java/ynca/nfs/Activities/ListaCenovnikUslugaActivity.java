package ynca.nfs.Activities;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

import ynca.nfs.Adapter.ListaCenovnikUslugaAdapter;
import ynca.nfs.Adapter.ListaVozilaAdapter;
import ynca.nfs.R;
import ynca.nfs.Models.Job;

public class ListaCenovnikUslugaActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseDatabase mFirebaseDatabase2;
    private DatabaseReference mDatabaseReference2;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ArrayList<Job> usluge;
    private EditText cena;
    private Button cenaSubmit;
    private Button dodajBtn;
    private Button submitBtn;
    private EditText vrstaUsuge;
    private EditText cenaUsluge;


    private final static int BROJ_USLUGA = 3;
    ListaCenovnikUslugaAdapter adapter;
    private RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cenovnik_usluga);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService")
                .child(mUser.getUid()).child("services");


        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference().child("Korisnik").child("VehicleService")
                .child(mUser.getUid()).child("services");


//        vrstaUsuge = (EditText) findViewById(R.id.opis_text_edit);
//        cenaUsluge = (EditText) findViewById(R.id.cena_edit_text);
//        submitBtn = (Button) findViewById(R.id.usluga_submit_btn);


        recyclerView = (RecyclerView) findViewById(R.id.lista_cenovnik_usluga_rv_id);

        usluge = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dodajBtn = (Button) findViewById(R.id.dodaj_uslugu_btn);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ListaCenovnikUslugaAdapter(new ListaVozilaAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {
            }
        });


        recyclerView.setAdapter(adapter);

        dodajBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v) {

                final Dialog d = new Dialog(v.getContext());
                d.setContentView(R.layout.dudaj_uslugu);
                d.setTitle(getResources().getString(R.string.NewService));
                vrstaUsuge = (EditText) d.findViewById(R.id.opis_text_edit);
                cenaUsluge = (EditText) d.findViewById(R.id.cena_edit_text);
                submitBtn = (Button) d.findViewById(R.id.usluga_submit_btn);
                d.show();
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        if(vrstaUsuge.getText().toString().trim().length() > 0 && cenaUsluge.getText().toString().trim().length() > 0 ) {

                            String vrstaToSave = vrstaUsuge.getText().toString();
                            int cenaToSave = Integer.valueOf(cenaUsluge.getText().toString());

                            Job u = new Job(vrstaToSave, cenaToSave, "");
                            mDatabaseReference2.push().setValue(u);

                        }
                        Toast.makeText(ListaCenovnikUslugaActivity.this, "All fields required!", Toast.LENGTH_SHORT).show();
                        d.dismiss();
                    }
                });
            }
        });



        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Job u = dataSnapshot.getValue(Job.class);
                u.setJobID(dataSnapshot.getKey());
                mDatabaseReference2.child(u.getJobID()).setValue(u);
                adapter.add(u);
                recyclerView.setAdapter(adapter);
                usluge.add(u);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Job u = dataSnapshot.getValue(Job.class);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
        recyclerView.setAdapter(adapter);

    }
}
