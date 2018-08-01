package ynca.nfs.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ynca.nfs.Adapter.ListaVozilaNaServisuAdapter;
import ynca.nfs.Models.Automobil;
import ynca.nfs.R;

public class ListaVozilaNaServisuActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ListaVozilaNaServisuAdapter theAdapter;
    private RecyclerView theRecyclerView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vozila_na_servisu);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Vozilo").child("Automobil");

        theRecyclerView = (RecyclerView) findViewById(R.id.lista_vozila_na_servisu_recycle_view_id);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        theRecyclerView.setLayoutManager(llm);
        theRecyclerView.setHasFixedSize(true);




        //TODO ISPRAVI OVO!
//        theAdapter = new ListaVozilaNaServisuActivity(4, new ListaVozilaAdapter.OnListItemClickListener() {
//            @Override
//            public void OnItemClick(int clickItemIndex) {
//                startActivity(new Intent(getBaseContext(),Car_info.class));
//            }
//        });
        theAdapter = new ListaVozilaNaServisuAdapter(new ListaVozilaNaServisuAdapter.OnListItemClickListener() {
            @Override
            public void OnItemClick(int clickItemIndex) {

            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Automobil a2 = dataSnapshot.getValue(Automobil.class);
                a2.setVoziloID(dataSnapshot.getKey());
                mDatabaseReference.child(a2.getVoziloID()).setValue(a2);
                theAdapter.add(a2);
                theRecyclerView.setAdapter(theAdapter);
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
}
