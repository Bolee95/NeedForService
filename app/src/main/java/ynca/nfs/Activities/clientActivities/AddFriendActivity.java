package ynca.nfs.Activities.clientActivities;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import  android.provider.Settings.Secure;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import ynca.nfs.Models.Client;
import ynca.nfs.R;

import static java.security.AccessController.getContext;

public class AddFriendActivity extends AppCompatActivity {

    //region Declarations

    //region Views

    private TextView availableDevicesTextView;
    private ListView availableDevicesListView;
    private Button ScanButton;
    //endregion

    //region Adapters
    private BluetoothAdapter bluetoothAdapter;
    private  ArrayAdapter<String> availableDevicesAdapter;
    //endregion

    //region HashMaps
    private HashMap<String, BluetoothDevice> availableBluetoothDevices;
    //endregion

    //region constants
    public static String DEVICE_ADDRESS = "deviceAddress";

    // Unique UUID for this application
    //private static final UUID MY_UUID_SECURE = UUID
    //        .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private static final UUID MY_UUID_SECURE = UUID
            .fromString("f00001101-0000-1000-8000-00805F9B34FB");


    private static final UUID MY_UUID_INSECURE = UUID
            .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");


    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    //region messageConstants
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    //endregion

    // region Constants that indicate the current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1; // listening connection
    public static final int STATE_CONNECTING = 2; // initiate outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // connected to remote device
    //endregion

    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    //endregion

    //region SharedPrefrences

    SharedPreferences sharedPreferences;
    String currentClientJson = "";
    Client currentClient;



    //endregion

    //region Firebase

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //endregion



    private int state;
    private BluetoothDevice currentlyConnectedDevice;
    private String mSocketType;
    private BluetoothSocket mmSocket;
    private ReceivingThread receivingThread;
    boolean friendshipSuccess=false;
    boolean  friendshipConfirmation=false;

    private InputStream inputStream;
    private OutputStream outputStream;
    String friendRequestUuidString;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_add_friend);



        getViewReferences();
        getCurrentClient();
        bindEventHandler();
        initializeValues();
        getDatabaseReferences();








    }

    private void getViewReferences()
    {

        availableDevicesTextView = (TextView) findViewById(R.id.BluetoothAvailableDevicesTextView);
        availableDevicesListView = (ListView) findViewById(R.id.BluetoothAvailableDevicesListView);
        ScanButton = (Button) findViewById(R.id.BluetoothScanButton);
    }

    private void bindEventHandler()
    {



        availableDevicesListView.setOnItemClickListener(mDeviceClickListener);


		ScanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startDiscovery();
				//ScanButton.setVisibility(View.GONE);
			}
		});

    }

    private void initializeValues() {


        availableDevicesAdapter = new ArrayAdapter<String>(this,	R.layout.device_name);


        availableDevicesListView.setAdapter(availableDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(discoverableIntent);

        }

    private void startDiscovery()
    {
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        availableBluetoothDevices = new HashMap<String, BluetoothDevice>();

        bluetoothAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long id) {
            bluetoothAdapter.cancelDiscovery();

            setProgressBarIndeterminateVisibility(true);



              String info = ((TextView) v).getText().toString();


            BluetoothDevice clickedDevice = availableBluetoothDevices.get(info);

            boolean pairedSuccess =pairDevice(clickedDevice);
            if (pairedSuccess)
            {
                addFriend(clickedDevice, info);

            }
        }
    };

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String foundDeviceAdress = (device.getName() + "\n"	+ device.getAddress());
                    availableBluetoothDevices.put(foundDeviceAdress ,device);
                    availableDevicesAdapter.add(foundDeviceAdress);


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (availableDevicesAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    availableDevicesAdapter.add(noDevices);
                    availableDevicesAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(discoveryFinishReceiver);
    }


    private void getCurrentClient()
    {
        sharedPreferences = getSharedPreferences("SharedData", MODE_PRIVATE);
        currentClientJson = sharedPreferences.getString("currentClient", "");

        if (!currentClientJson.isEmpty()) {
            Gson gsonInstance = new Gson();
           currentClient = gsonInstance.fromJson(currentClientJson, Client.class);

    }
    }

    private  void getDatabaseReferences()
    {
        firebaseDatabase =  FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Korisnik")
                .child("Client").child(currentClient.getUID())
                .child("listOfFriendsUIDs");


    }

    private void addFriend(BluetoothDevice device, String info)
    {
        if(bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        BluetoothSocket tempSocket = null;
        friendshipSuccess = false;


        BluetoothSocket socket = null;

        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
        } catch (Exception e) {
            Log.e("","Error creating socket");}

        try {
            socket.connect();
            Log.e("","Connected");
        } catch (IOException e) {
            Log.e("", e.getMessage());
            try {
                Log.e("", "trying fallback...");

                socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                socket.connect();

                Log.e("", "Connected");
            } catch (Exception e2) {
                Log.e("", "Couldn't establish Bluetooth connection!");
            }
        }

        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;
        mmSocket = tempSocket;
        try {
            tempInputStream  = mmSocket.getInputStream();
            tempOutputStream = mmSocket.getOutputStream();
        }
        catch(IOException e){
            e.getStackTrace();
        }
        receivingThread = new ReceivingThread(tempInputStream);
        receivingThread.start();
        outputStream = tempOutputStream;

        byte[] uuidBinary = currentClient.getUID().getBytes();
        try {
            outputStream.write(uuidBinary);
        }
        catch (IOException e)
        {

        }

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        while (!friendshipSuccess || (elapsedTime < 2*1000))
        {
            elapsedTime = (new Date()).getTime() - startTime;
        }


        if(friendshipSuccess)
        {
            receivingThread.cancel();
            ConfirmationThread confirmationThread = new ConfirmationThread(inputStream);
            confirmationThread.run();
            friendshipSuccess=false;
            try {
                outputStream.write( new byte[]{(byte) 1 });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            {
                ConfirmationThread confirmationThread = new ConfirmationThread(inputStream);
                confirmationThread.run();
                friendshipSuccess=false;
                try {
                    outputStream.write( new byte[]{(byte) 0 });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(), R.string.SomethingWentWrong, Toast.LENGTH_LONG);

            }

        while (!friendshipSuccess || (elapsedTime < 2*1000))
        {
            elapsedTime = (new Date()).getTime() - startTime;
        }


        try {
            outputStream.write( new byte[]{(byte) 1 });
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(friendshipConfirmation)
        {
            Toast.makeText(getBaseContext(), "You are now friends with " + info, Toast.LENGTH_LONG  );
        }




        try {
            outputStream.close();
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unpairDevice(device);
        setProgressBarIndeterminate(false);
    }









    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {

        }
    }

    private boolean pairDevice(BluetoothDevice device) {
            Boolean bool = false;
            try {

                Class cl = Class.forName("android.bluetooth.BluetoothDevice");
                Class[] par = {};
                Method method = cl.getMethod("createBond", par);
                Object[] args = {};
                bool = (Boolean) method.invoke(device);
            } catch (Exception e) {

            }
            return bool.booleanValue();
        }



        private class ReceivingThread extends Thread
    {
        InputStream ThreadInputStream;

        public ReceivingThread(InputStream inStream )
        {
            ThreadInputStream = inputStream;

        }

        public void run()
        {
            byte[] recvBytes = new byte[256];
            int bytes=0;



            try {
                while ((bytes!=16)) {
                    bytes = inputStream.available();

                }

                inputStream.read(recvBytes);




            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                friendRequestUuidString = new String(recvBytes, "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            DatabaseReference addedFriendReference = databaseReference.push();
            if(currentClient.getListOfFriendsUIDs().containsValue(friendRequestUuidString))
            {
                Toast.makeText(getBaseContext(), "The user is already your friend", Toast.LENGTH_SHORT);
                setProgressBarIndeterminateVisibility(false);


            }
            else
                addedFriendReference.setValue(UUID.fromString(friendRequestUuidString));

            friendshipSuccess=true;


    }

    public void cancel()
    {
        try{
            ThreadInputStream.close();
        }
        catch (IOException e)
        {

        }
    }

    }


    private class ConfirmationThread
    {
        InputStream ConfirmationStream;
        public  ConfirmationThread(InputStream inStream)
        {
            ConfirmationStream = inStream;

        }

        public void run()
        {
            byte[] recvBytes = new byte[256];
            int bytes=0;



            try {
                while (bytes==0) {
                    bytes = inputStream.available();

                }

                inputStream.read(recvBytes);

                friendshipConfirmation = recvBytes[0]!=0;




            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public  void  cancel()
        {
            try {
                ConfirmationStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }










}




