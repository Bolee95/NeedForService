package ynca.nfs.Activities.clientActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ynca.nfs.Adapter.ListaVozilaAdapter;
import ynca.nfs.Models.Vehicle;
import ynca.nfs.R;

public class ListaVozilaActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ListaVozilaAdapter theAdapter;
    private RecyclerView theRecyclerView;
    private ArrayList<Vehicle> listOfVehicles;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vozila);

        theRecyclerView = (RecyclerView) findViewById(R.id.lista_vozila_recycle_view_id);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        theRecyclerView.setLayoutManager(llm);
        theRecyclerView.setHasFixedSize(true);
        listOfVehicles = new ArrayList<>();


        //Toolbar podesavanja
        toolbar = (Toolbar) findViewById(R.id.carListToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_18dp);
        getSupportActionBar().setTitle("");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Client")
                .child(mUser.getUid()).child("listOfCars");


        theAdapter = new ListaVozilaAdapter(new ListaVozilaAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {
                Vehicle temp = listOfVehicles.get(clickItemIndex);
                Intent intent = new Intent(getBaseContext(),carInfoActivity.class);
                intent.putExtra("Registarski",temp.getRegistyNumber());
                intent.putExtra("vozilo", listOfVehicles.get(clickItemIndex));
                startActivity(intent);
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Vehicle a = dataSnapshot.getValue(Vehicle.class);
                a.setVehicleID(dataSnapshot.getKey());
                mDatabaseReference.child(a.getVehicleID()).setValue(a);
                theAdapter.add(a);
                theRecyclerView.setAdapter(theAdapter);

                listOfVehicles.add(a);

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
        theRecyclerView.setAdapter(theAdapter);
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
