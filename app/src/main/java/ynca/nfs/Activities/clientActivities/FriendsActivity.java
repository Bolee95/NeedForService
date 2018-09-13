package ynca.nfs.Activities.clientActivities;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ynca.nfs.Adapter.FriendsListAdapter;
import ynca.nfs.ChatService;
import ynca.nfs.Models.Client;
import ynca.nfs.R;

public class FriendsActivity extends AppCompatActivity implements FriendsListAdapter.OnItemsClickListener {

    private BluetoothAdapter bluetoothAdapter = null;
    private static final int BT_DISCOVERABLE_TIME = 240;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private String connectedDeviceName = null;
    private StringBuffer outStringBuffer;

    String idUser;

    private static Client currentClient;
    private HashMap<String, String> currentClientFriends;
    public static ArrayList<Client> friends;// = new ArrayList<Client>();
    public static ArrayList<Client> notFriends;// = new ArrayList<>();
    private ArrayList<Client> searchFriendsUnfiltered;
    private ArrayList<Client> searchFriendsFiltered;
    private ArrayList<Client> searchFriendsSorted;
    private final static String TAG = "BT";
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public  static  final  int SORT_BY_NAME_ASCENDING= 0;
    public static final  int SORT_BY_NAME_DESCENDING= 1;
    public static  final  int SORT_BY_POINTS=  2;
    public static final  int SORT_BY_DISTANCE =  3;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private ChatService chatService = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceListOfFriends;



    //region Views Declarations
    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private MenuItem searchItem;
    private SearchView searchView;
    private Spinner spinner;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        currentClientFriends = new HashMap<String, String>();
        friends = new ArrayList<Client>();
        notFriends = new ArrayList<>();
        //currentClientFriends = new HashMap<String, String>();
        fetchCurrentClient();
        setupUI();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        idUser = user.getUid();



        databaseReference = FirebaseDatabase.getInstance().getReference("Korisnik").child("Client");
        databaseReferenceListOfFriends = databaseReference.child(user.getUid()).child("listOfFriendsUIDs");

        getAllFriends();
        setUpListeners();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_menu, menu);

        //region searchField event listeners and init
        searchItem = menu.findItem(R.id.searchFriends);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //todo promeniti raspored linija tako da moze da se ubaci sotr
            @Override
            public boolean onQueryTextChange(String newText) {
                searchFriendsFiltered =  new ArrayList<Client>();
                FriendsListAdapter filteredAdapter = new FriendsListAdapter(FriendsActivity.this,
                        currentClient.getLastKnownLat(), currentClient.getLastKnownlongi());
                recyclerView.setAdapter(filteredAdapter);
                if(!newText.isEmpty())
                {
                searchFriendsUnfiltered = new ArrayList<Client>();
                searchFriendsUnfiltered = (ArrayList<Client>) friends.clone();
                searchFriendsSorted = new ArrayList<>();

                for ( Client client : searchFriendsUnfiltered  )
                {
                    if (client.getFirstName().contains(newText) || client.getLastName().contains(newText)
                            || client.getEmail().contains(newText))
                        searchFriendsFiltered.add(client);
                }
                switch (spinner.getSelectedItemPosition()) {

                        case SORT_BY_NAME_ASCENDING:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new LexicographicComparatorAsc());
                        break;
                        case SORT_BY_NAME_DESCENDING:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new LexicofraphicComparatorDesc());
                        break;
                        case SORT_BY_POINTS:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new PointsComparator());
                        break;
                        case SORT_BY_DISTANCE:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new DistanceComparator());
                        break;
                        default: break;

                        }
                    for (Client c: searchFriendsSorted) {
                        filteredAdapter.add(c);
                    }

                recyclerView.setAdapter(filteredAdapter);
                filteredAdapter.notifyDataSetChanged();

                }
                else
                {
                    recyclerView.setAdapter(FriendsActivity.this.adapter);
                    FriendsActivity.this.adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addFriends) {
            redirectToDeviceList();
        } else if (id == R.id.searchFriends) {
            //pretrazivanje prijatelja
        } else {
            //back dugme
            finish();
        }
        return super.onOptionsItemSelected(item);


    }


    @Override
    protected void onResume() {
        super.onResume();
        //if (currentClientFriends.size() != 0) {
        //    currentClientFriends.clear();
        //}
    }

    private void saveNewFriend(final String friendId){

        final DatabaseReference ref = databaseReference.child(user.getUid()).child("listOfFriendsUIDs");

        if (!currentClientFriends.containsValue(friendId)) {
            ref.push().setValue(friendId);
            Toast.makeText(FriendsActivity.this, "You got new friend!", Toast.LENGTH_SHORT).show();

            currentClientFriends.put(friendId, friendId);
            for ( Client client : notFriends) {
                if (client.getUID().equals(friendId))
                {
                    friends.add(client);
                    notFriends.remove(client);
                    adapter.add(client);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    return;
                }

            }
            //sortFriends(spinner.getSelectedItemPosition());


        }
        else
        {
            Toast.makeText(FriendsActivity.this, "You are already friends!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllFriends() {
        friends.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Client client = dataSnapshot.getValue(Client.class);

                if (currentClientFriends == null)
                    currentClientFriends = new HashMap<String, String>();
                if (currentClientFriends.size() != 0) {
                    if (currentClientFriends.containsValue(client.getUID())) {

                        recyclerView.setAdapter(adapter);
                        adapter.add(client);
                        friends.add(client);
                        adapter.notifyDataSetChanged();
                        //recyclerView.getAdapter().notifyDataSetChanged();

                    }
                    else {
                        notFriends.add(client);
                    }
                }
                else
                    notFriends.add(client);
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

    public void redirectToDeviceList()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(FriendsActivity.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }
        ensureDiscoverable(bluetoothAdapter);
    }



    private void ensureDiscoverable(BluetoothAdapter bluetoothAdapter) {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BT_DISCOVERABLE_TIME);
        startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == BT_DISCOVERABLE_TIME) {
                    //Toast.makeText(this,"Setup chat", Toast.LENGTH_SHORT).show();
                    setupChat();
                    addNewFriend();
                } else {
                    // Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    //finish();
                }
                break;
        }
    }
    private void connectDevice(Intent data, boolean secure) {

        String address = data.getExtras().getString(DeviceListActivity.DEVICE_ADDRESS);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        try {
            chatService.connect(device, secure);
        } catch (Exception e) {
            Toast.makeText(this, "Error! Other user must click on + button.", Toast.LENGTH_LONG).show();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
    }
    private void addNewFriend() {
        // Log.d(TAG, "Friends addNewFriend started");
        Runnable r = new Runnable() {

            @Override
            public void run() {
                Intent serverIntent = new Intent(FriendsActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            }
        };
        Thread btThread = new Thread(r);
        btThread.start();
    }


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "MainActivity: handleMessage started");
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatService.STATE_CONNECTED:
                            Log.d(TAG, "MainActivity: handleMessage MESSAGE_STATE_CHANGE STATE_CONNECTED");     //for new devices
                            sendFriendRequest();
                            break;
                        case ChatService.STATE_CONNECTING:
                            Log.d(TAG, "MainActivity: handleMessage MESSAGE_STATE_CHANGE STATE_CONNECTING");    //for paired devices??
                            sendFriendRequest();
                            break;
                        case ChatService.STATE_LISTEN:
                        case ChatService.STATE_NONE:
                            Log.d(TAG, "MainActivity: handleMessage MESSAGE_STATE_CHANGE STATE_NONE");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    Log.d(TAG, "MainActivity: handleMessage MESSAGE_WRITE");
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    Log.d(TAG, "MainActivity: handleMessage MESSAGE_READ");
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);


                    Log.d(TAG, "readMessage:" + readMessage);


                    final String message = readMessage;

                    int _char = message.lastIndexOf("_");
                    String messageCheck = message.substring(0, _char + 1);
                    final String friendsUid = message.substring(_char + 1);


                    runOnUiThread(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(FriendsActivity.this)
                                    .setTitle("FRIEND REQUEST")
                                    .setMessage("Are you sure you want to become friends with a device " + connectedDeviceName + "?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            String idReceive = message;
                                            //Toast.makeText(FriendsActivity.this, "Your friend id: " + message, Toast.LENGTH_LONG).show();
                                            //allreadyFriends(idReceive);
                                            if(allreadyFriends(idReceive)){
                                                Toast.makeText(FriendsActivity.this, "You are already friends", Toast.LENGTH_LONG).show();
                                            }
                                            else {

                                                saveNewFriend(idReceive);
                                            }

                                            //     adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(FriendsActivity.this, "You declined friend request", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                    //}
                    break;
                case MESSAGE_DEVICE_NAME:
                    //Log.d(TAG, "MainActivity: handleMessage MESSAGE_DEVICE_NAME");
                    connectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectedDeviceName, Toast.LENGTH_LONG).show();
                    break;

                case MESSAGE_TOAST:
                    // Log.d(TAG, "MainActivity: handleMessage MESSAGE_TOAST");
                    //  Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    public boolean allreadyFriends(String id) {

        boolean yes = false;

        yes = currentClientFriends.containsValue(id);

        return yes;
    }


    private void sendFriendRequest() {
        String message = idUser.toString();
        Log.d(TAG, "MainActivity: addNewFriend sendingMessage:" + message);
        sendMessage(message);
    }


    private void sendMessage(String message) {

        if (chatService.getState() != ChatService.STATE_CONNECTED) {
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatService.write(send);
            outStringBuffer.setLength(0);
        }
    }


    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    private boolean setupChat() {
        Log.d(TAG, "MainActivity: setupChat started");
        chatService = new ChatService(this, handler);
        outStringBuffer = new StringBuffer("");
        if (chatService.getState() == ChatService.STATE_NONE) {
            chatService.start();
        }
        return true;
    }

    @Override
    public void OnItemClick(int clickItemIndex) {

        Intent clientIntent = new Intent(this, clientInfoActivity.class);
        clientIntent.putExtra("editable", false);

        SharedPreferences settings = getSharedPreferences("SharedData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(friends.get(clickItemIndex));
        prefEditor.putString("clientInfo", json);
        prefEditor.commit();
        startActivity(clientIntent);

    }

    private void fetchCurrentClient ()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedData", MODE_PRIVATE);
        String currentClientJson = sharedPreferences.getString("currentClient", "");

        if (!currentClientJson.isEmpty()) {
            Gson gsonInstance = new Gson();
            currentClient = gsonInstance.fromJson(currentClientJson, Client.class);
            currentClientFriends = currentClient.getListOfFriendsUIDs();

        }
    }

    private void setupUI(){
        adapter = new FriendsListAdapter( this, currentClient.getLastKnownLat(),
                currentClient.getLastKnownlongi());


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


        ArrayList<String> spinnerItems = new  ArrayList<String>();
        spinnerItems.add("Name ascending");
        spinnerItems.add("Name descending");
        spinnerItems.add("Points");
        spinnerItems.add("Distance");


        //region spinner
        spinner = (Spinner) findViewById(R.id.sortBySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,  spinnerItems);
       // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortFriends(position);

        }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);
        //endregion



    }

    class LexicographicComparatorAsc implements Comparator<Client>
    {

        @Override
        public int compare(Client o1, Client o2) {
            return (o1.getFirstName() + " " + o1.getLastName())
                    .compareToIgnoreCase(o2.getFirstName() + " " + o2.getLastName());
        }
    }

    class  LexicofraphicComparatorDesc implements  Comparator<Client> {

        @Override
        public int compare(Client o1, Client o2) {
            return -((o1.getFirstName() + " " + o1.getLastName())
                    .compareToIgnoreCase(o2.getFirstName() + " " + o2.getLastName()));
        }
    }

    class PointsComparator implements  Comparator<Client>{
        @Override
        public int compare(Client o1, Client o2) {
            return -(o1.getPoints()-o2.getPoints());
        }
    }

    class DistanceComparator implements  Comparator<Client>
    {


        @Override
        public int compare(Client o1, Client o2) {

            float distance1 [] = new float[1];
            float distance2 [] = new float[1];

            Location.distanceBetween(currentClient.getLastKnownLat(), currentClient.getLastKnownlongi(),
            o1.getLastKnownLat(), o1.getLastKnownlongi(), distance1);
            Location.distanceBetween(currentClient.getLastKnownLat(),
                    currentClient.getLastKnownlongi(), o2.getLastKnownLat(), o2.getLastKnownlongi(), distance2);

            return (distance1[0]-distance2[0]<0)? -1:1;
        }
    }


    public ArrayList<Client> sortClientList(ArrayList<Client> listArg, Comparator<Client> comparator)
    {
        ArrayList<Client>  returnList = (ArrayList<Client>) listArg.clone();
        Collections.sort(returnList,comparator);
        /*
       while (!list.isEmpty())
        {
            int itemPosition;
            Client temp=list.get(0);
            for (itemPosition=0; itemPosition<list.size()-1; itemPosition++)
            {
                if (comparator.compare(list.get(itemPosition), list.get(itemPosition+1))>0)
                    temp = list.get(itemPosition+1);
                else
                    temp=list.get(itemPosition);
            }
            returnList.add(temp);
            list.remove(temp);

        }
        */
        return returnList;

        }

        public  void setUpListeners()
        {
        }

        public  void sortFriends(int spinnerOption)
        {
            searchFriendsSorted = new ArrayList<>();
            if(searchView.getQuery().length()==0){
            switch (spinnerOption)
            {
                case SORT_BY_NAME_ASCENDING:
                    searchFriendsSorted = sortClientList(friends, new LexicographicComparatorAsc());
                    break;
                case SORT_BY_NAME_DESCENDING:
                    searchFriendsSorted = sortClientList(friends, new LexicofraphicComparatorDesc());
                    break;
                case SORT_BY_POINTS:
                    searchFriendsSorted = sortClientList(friends, new PointsComparator());
                    break;
                case SORT_BY_DISTANCE:
                    searchFriendsSorted = sortClientList(friends, new DistanceComparator());
                    break;
                default: break;
            }
            adapter = new FriendsListAdapter(this, currentClient.getLastKnownLat(),
                    currentClient.getLastKnownlongi());
                for (Client c: searchFriendsSorted) {
                    adapter.add(c);

                }
                recyclerView.setAdapter(adapter);
        }
        else
            {
                switch (spinnerOption)
                {
                    case SORT_BY_NAME_ASCENDING:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new LexicographicComparatorAsc());
                        break;
                    case SORT_BY_NAME_DESCENDING:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new LexicofraphicComparatorDesc());
                        break;
                    case SORT_BY_POINTS:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new PointsComparator());
                        break;
                    case SORT_BY_DISTANCE:
                        searchFriendsSorted = sortClientList(searchFriendsFiltered, new DistanceComparator());
                        break;
                    default: break;
                }
                adapter = new FriendsListAdapter(this, currentClient.getLastKnownLat(),
                        currentClient.getLastKnownlongi());
                for (Client c: searchFriendsSorted) {
                    adapter.add(c);

                }
                recyclerView.setAdapter(adapter);
            }



            recyclerView.getAdapter().notifyDataSetChanged();
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}