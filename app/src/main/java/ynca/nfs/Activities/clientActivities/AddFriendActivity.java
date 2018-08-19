package ynca.nfs.Activities.clientActivities;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import  android.provider.Settings.Secure;

import java.lang.reflect.Method;
import java.util.Set;

import ynca.nfs.R;

import static java.security.AccessController.getContext;

public class AddFriendActivity extends AppCompatActivity {

    //region Declarations

    //region Views
    private TextView pairedDevicesTextView;
    private TextView availableDevicesTextView;
    private ListView pairedDevicesListView;
    private ListView availableDevicesListView;
    private Button ScanButton;
    //endregion

    //region Adapters
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private  ArrayAdapter<String> availableDevicesAdapter;
    //endregion

    public static String DEVICE_ADDRESS = "deviceAddress";

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_add_friend);

        getViewReferences();
        bindEventHandler();
        initializeValues();






    }

    private void getViewReferences()
    {
        pairedDevicesTextView = (TextView) findViewById(R.id.PairedDeviceTextView);
        pairedDevicesListView = (ListView) findViewById(R.id.PairedDeviceListView);
        availableDevicesTextView = (TextView) findViewById(R.id.PairedDeviceTextView);
        availableDevicesListView = (ListView) findViewById(R.id.PairedDeviceListView);
        ScanButton = (Button) findViewById(R.id.DeviceScanBtn);
    }

    private void bindEventHandler()
    {


        pairedDevicesListView.setOnItemClickListener(mDeviceClickListener);
        availableDevicesListView.setOnItemClickListener(mDeviceClickListener);


		ScanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startDiscovery();
				//ScanButton.setVisibility(View.GONE);
			}
		});

    }

    private void initializeValues() {

        pairedDevicesAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        availableDevicesAdapter = new ArrayAdapter<String>(this,	R.layout.device_name);

        pairedDevicesListView.setAdapter(pairedDevicesAdapter);
        availableDevicesListView.setAdapter(availableDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            //tvDeviceListPairedDeviceTitle.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                unpairDevice(device); //Dirty fix because app sometimes crashes when connecting to a paired device.
                //pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesAdapter.add(noDevices);
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {

        }
    }

    private void startDiscovery()
    {
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        if(bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            bluetoothAdapter.cancelDiscovery();

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent intent = new Intent();
            intent.putExtra(DEVICE_ADDRESS, address);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    availableDevicesAdapter.add(device.getName() + "\n"	+ device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (availableDevicesAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    availableDevicesAdapter.add(noDevices);
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
}
