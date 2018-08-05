package ynca.nfs.Activities.clientActivities;

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

import java.util.HashMap;

import ynca.nfs.Activities.Poruka_tekst_activity;
import ynca.nfs.Activities.mainScreensActivities.mainScreenClientActivity;
import ynca.nfs.Adapter.CustomPorukeAdapter;
import ynca.nfs.Models.Poruka;
import ynca.nfs.R;

/**
 * Created by Nikola on 5/27/2017.
 */

public class Client_Inbox_Activity  extends AppCompatActivity {
    private static  int BROJ_PORUKA = 0;



    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseDatabase mFirebaseDatabase2;
    private DatabaseReference mDatabaseReference2;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    private HashMap<String, Poruka> poruke;
    private RecyclerView recyclerView;
    private CustomPorukeAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_client);



        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


       // Main_screen_client.resetujBrojProcitanihPoruka(); // ovo je zbog inbox dugmeta na main-u

        // Poruke = new ArrayList<Poruka>();
        //CustomPorukeAdapter adapter = new CustomPorukeAdapter(this, Poruke);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        poruke = new HashMap<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik")
                .child("Client").child(mUser.getUid()).child("primljenePoruke");

        SharedPreferences sp1 = getSharedPreferences("SharedData", MODE_PRIVATE);

        int brPor = -1;
        sp1.getInt("brojPoruka", brPor);
        if(brPor == 0){

            Toast.makeText(getApplicationContext(), R.string.noMessages, Toast.LENGTH_LONG);

        }

        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mDatabaseReference2 = mFirebaseDatabase2.getReference().child("Korisnik")
                .child("Client").child(mUser.getUid()).child("primljenePoruke");
        recyclerView = (RecyclerView) findViewById(R.id.inbox_clinet_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CustomPorukeAdapter(
                new CustomPorukeAdapter.OnListItemClickListener() {
                    @Override
                    public void OnItemClick(int clickItemIndex) {
                    //    Toast.makeText(getApplicationContext(), "Clicked on item:" + clickItemIndex, Toast.LENGTH_LONG).show();
                        //adapter.getPorukabyIndex(clickItemIndex).setProcitana(true); NE U ADAPTER NEGO U HASHMAP U OVU KLASU
                        Intent i = new Intent(getBaseContext(), Poruka_tekst_activity.class);
                        String id = adapter.getPorukabyIndex(clickItemIndex).getId();
                        Poruka p = poruke.get(id);
                        if(p.isProcitana())
                        {
                            i.putExtra("porukaBilaProcitana", true);
                        }
                        else
                        {
                            i.putExtra("porukaBilaProcitana", false);
                        }
                        p.oznaciKaoProcitanu();
                        mDatabaseReference2.child(p.getId()).setValue(p);
                       // Poruka por = adapter.getPorukabyIndex(clickItemIndex);
                        i.putExtra("PorukaZaCitanje", p);
                        i.putExtra("BROJ_PORUKA",BROJ_PORUKA);
                        startActivity(i);

                    }
                });


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Poruka p = dataSnapshot.getValue(Poruka.class);
                poruke.put(dataSnapshot.getKey(), p);
                p.setId(dataSnapshot.getKey());
                mDatabaseReference2.child(p.getId()).setValue(p);
                adapter.add(p);
                recyclerView.setAdapter(adapter);
                BROJ_PORUKA++;

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


        recyclerView.setAdapter(adapter);

     

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(),mainScreenClientActivity.class));
    }


   // @Override
    //public void onResume()
    //{
     //   super.onResume();

    //}




}




