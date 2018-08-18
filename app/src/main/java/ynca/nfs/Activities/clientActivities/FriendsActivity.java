package ynca.nfs.Activities.clientActivities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import ynca.nfs.Adapter.FriendsListAdapter;
import ynca.nfs.Models.Client;
import ynca.nfs.R;

public class FriendsActivity extends AppCompatActivity implements  FriendsListAdapter.OnItemsClickListener {

    //region Declarations

    private int REQUEST_ENABLE_BT = 1;

    private static Client currentClient;

    //region Firebase Declarations
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceFriendsUIDs;
    private DatabaseReference mDatabaseReferenceClients;
    //endregion

    private ArrayList<Client> friends;

    //region Views Declarations
    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private SearchView searchView;

    //endregion

    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        friends = new ArrayList<Client>();

        fetchCurrentClient();


        //region Toolbar podesavanja
        Toolbar toolbar = (Toolbar) findViewById(R.id.friendsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");


//endregion


        recyclerView = (RecyclerView) findViewById(R.id.FriendsListRecycleView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addFriends) {
            //kliknuta opcija za dodavanje novog prijatelja

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else
                startActivity(new Intent(getBaseContext(), AddFriendActivity.class));
        } else if (id == R.id.searchFriends) {
            //pretrazivanje prijatelja
        } else {
            //back dugme
            finish();
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    public void OnItemClick(int clickItemIndex) {
        final String friendUID = friends.get(clickItemIndex).getUID();
        Intent intent = new Intent(getBaseContext(), FriendProfileActivity.class);
        intent.putExtra("friendUID", friendUID);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(getBaseContext(), R.string.BluetoothEnabled, Toast.LENGTH_SHORT);
                startActivity(new Intent(getBaseContext(), AddFriendActivity.class));
            } else if (REQUEST_ENABLE_BT == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), R.string.BluetoothRequestCanceled,
                        Toast.LENGTH_LONG);
            }

        }

    }

    private void getFirebaseReferences() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceClients = mFirebaseDatabase.getReference().child("Korisnik").child("Client");


        mDatabaseReferenceClients.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HashMap<String, String> currentClientFriends = currentClient.getListOfFriendsUIDs();
                Client client = dataSnapshot.getValue(Client.class);
                if (currentClientFriends.containsValue(client.getUID())) {
                    adapter.add(client);
                    recyclerView.setAdapter(adapter);
                    friends.add(client);

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
        private void fetchCurrentClient ()
        {


            SharedPreferences sharedPreferences = getSharedPreferences("SharedData", MODE_PRIVATE);
            String currentClientJson = sharedPreferences.getString("TrenutniKlijent", "");

            if (!currentClientJson.isEmpty()) {
                Gson gsonInstance = new Gson();
                currentClient = gsonInstance.fromJson(currentClientJson, Client.class);
            }


        }
    }

