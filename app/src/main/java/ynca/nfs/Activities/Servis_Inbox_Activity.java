package ynca.nfs.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import ynca.nfs.Activities.mainScreensActivities.MainScreenServisActivity;
import ynca.nfs.Adapter.CustomPorukeServisAdapter;
import ynca.nfs.Models.Client;
import ynca.nfs.Models.Poruka;
import ynca.nfs.R;

/**
 * Created by Nikola on 5/29/2017.
 */

public class Servis_Inbox_Activity  extends AppCompatActivity {

    private static int BROJ_PORUKA = 0;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private RecyclerView recyclerView;
    private CustomPorukeServisAdapter adapter;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_servis);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        SharedPreferences sp1 = getSharedPreferences("SharedData", MODE_PRIVATE);

        int brPor = -1;
        sp1.getInt("brojPoruka", brPor);
        if(brPor == 0){

            Toast.makeText(getApplicationContext(), R.string.noMessages, Toast.LENGTH_LONG);

        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik")
                .child("VehicleService").child(mUser.getUid()).child("primljenePoruke");


        recyclerView = (RecyclerView) findViewById(R.id.inbox_servis_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CustomPorukeServisAdapter(new CustomPorukeServisAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {
              //  Toast.makeText(getApplicationContext(), "Clicked on item:" + clickItemIndex, Toast.LENGTH_LONG).show();

                SharedPreferences sp = getSharedPreferences("SharedData", MODE_PRIVATE);

                Poruka por = adapter.getPorukabyIndex(clickItemIndex);
                por.setProcitana(true);

                Gson gson = new Gson();
                String json = sp.getString("TrenutniKlijent", "");
                Client k = gson.fromJson(json, Client.class);

                //// TODO: 6/1/2017  k je klijent kod koga poruka por treba da se update-uje

                mDatabaseReference.child(por.getId()).setValue(por);

                Intent i = new Intent(getBaseContext(), Poruka_tekst_activity.class);
                i.putExtra("PorukaZaCitanje", por);
                startActivity(i);

            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Poruka p = dataSnapshot.getValue(Poruka.class);
                adapter.add(p);
                p.setId(dataSnapshot.getKey());
                mDatabaseReference.child(p.getId()).setValue(p);
                recyclerView.setAdapter(adapter);
                BROJ_PORUKA++;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                BROJ_PORUKA--;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(Servis_Inbox_Activity.this, MainScreenServisActivity.class));
        finish();

    }
}
