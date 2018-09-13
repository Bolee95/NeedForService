package ynca.nfs.Activities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ynca.nfs.Adapter.ListaRecenzijaAdapter;
import ynca.nfs.Models.Review;
import ynca.nfs.R;

public class Lista_Recenzija_Activity extends AppCompatActivity {
    ListaRecenzijaAdapter adapter;
    private RecyclerView recyclerView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    ArrayList<Review> recenzije;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__recenzija_);

        recyclerView = (RecyclerView) findViewById(R.id.lista_recenzija_rv_id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        recenzije = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("VehicleService")
                .child(mUser.getUid()).child("reviews");

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ListaRecenzijaAdapter(new ListaRecenzijaAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {
                //startActivity(new Intent(getBaseContext(), Car_info.class));

            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Review r = dataSnapshot.getValue(Review.class);
                adapter.add(r);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
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
