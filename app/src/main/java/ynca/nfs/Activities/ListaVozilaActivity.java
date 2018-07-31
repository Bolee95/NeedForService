package ynca.nfs.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import ynca.nfs.Adapter.ListaVozilaAdapter;
import ynca.nfs.Automobil;
import ynca.nfs.R;

public class ListaVozilaActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ListaVozilaAdapter theAdapter;
    private RecyclerView theRecyclerView;
    private ArrayList<Automobil> lista;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vozila);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));

        theRecyclerView = (RecyclerView) findViewById(R.id.lista_vozila_recycle_view_id);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        theRecyclerView.setLayoutManager(llm);
        theRecyclerView.setHasFixedSize(true);
        lista = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik").child("Klijent")
                .child(mUser.getUid()).child("listaVozila");




        //TODO ISPRAVI OVO!
        theAdapter = new ListaVozilaAdapter(new ListaVozilaAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {
                Automobil temp = lista.get(clickItemIndex);
                Intent intent = new Intent(getBaseContext(),Car_info.class);
                intent.putExtra("Registarski",temp.getRegBroj());
                intent.putExtra("vozilo", lista.get(clickItemIndex));
                startActivity(intent);
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Automobil a = dataSnapshot.getValue(Automobil.class);
                a.setVoziloID(dataSnapshot.getKey());
                mDatabaseReference.child(a.getVoziloID()).setValue(a);
                theAdapter.add(a);
                theRecyclerView.setAdapter(theAdapter);

                lista.add(a);

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
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ListaVozilaActivity.this, Info_client.class));

    }
}
