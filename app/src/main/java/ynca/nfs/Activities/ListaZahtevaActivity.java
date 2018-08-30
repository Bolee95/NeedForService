package ynca.nfs.Activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ynca.nfs.Activities.clientActivities.carInfoActivity;
import ynca.nfs.Adapter.ListaVozilaAdapter;
import ynca.nfs.Adapter.ListaZahtevaAdapter;
import ynca.nfs.Models.Request;
import ynca.nfs.R;

public class ListaZahtevaActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseDatabase mFirebaseDatabase2;
    private DatabaseReference mDatabaseReference2;

    Button acceptBtn;
    Button declineBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    ListaZahtevaAdapter adapter;
    private RecyclerView recyclerView;

    ArrayList<Request> zahtevi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_zahteva);

        recyclerView = (RecyclerView) findViewById(R.id.lista_zahteva_recycle_view_id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        zahtevi = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("ServiceRequests")
                .child(mUser.getUid());

        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference();

        acceptBtn = (Button) findViewById(R.id.zahtev_btn_accept);
        declineBtn = (Button) findViewById(R.id.zahtev_btn_decline);



        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ListaZahtevaAdapter(new ListaVozilaAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {
                startActivity(new Intent(getBaseContext(),carInfoActivity.class));
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Request z = dataSnapshot.getValue(Request.class);
                adapter.add(z);
                z.setId(dataSnapshot.getKey());
//                mDatabaseReference2.child("ZahteviKlijent").child(z.getIdKlijenta()).child(z.getId()))
//                            .child("zahtevi").child(z.getId()).child("idKlijenta").setValue(z.getIdKlijenta());
                zahtevi.add(z);

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
