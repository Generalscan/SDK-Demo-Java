package com.generalscan.sdkdemo.ui.activity.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.generalscan.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceListActivity extends AppCompatActivity {

    private static final String TAG = "DeviceList";
    private static final int REQUEST_FINE_LOCATION = 0;
    //region variables
    private ListView mListView;
    private final List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private BluetoothDeviceListActivity.DeviceListAdapter mListAdapter;
    private ProgressBar mProgressBar;
    private BluetoothAdapter mBluetoothAdapter;
    private TextView mScanButton;
    private boolean mScanning;

    // The on-click listener for all devices in the ListViews
    private final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            BluetoothDevice device = mDeviceList.get(position);
            // Cancel discovery because it's costly and we're about to connect
            if (mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();

            Intent intent = new Intent();
            intent.putExtra("Address", device.getAddress());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //in some case BLE recognized as MISC
                int majorDeviceClass = device.getBluetoothClass().getMajorDeviceClass();
                if (
                        majorDeviceClass == BluetoothClass.Device.Major.MISC
                                || majorDeviceClass == BluetoothClass.Device.Major.PERIPHERAL
                                || majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED)
                    mDeviceList.add(device);
                mListAdapter.notifyDataSetChanged();

                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                setTitle("Select a device to connect");
                mScanning = false;
                mScanButton.setText(R.string.start_scan);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);

        mProgressBar = findViewById(R.id.progressBar);
        mScanButton = findViewById(R.id.button_scan);
        mListView = findViewById(R.id.list_devices);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mayRequestLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The requested permission is granted.
                if (!mScanning) {
                    scanLeDevice();
                }
            } else {
                // The user disallowed the requested permission.
            }
        }
    }

    //region discovery functions
    private void scanLeDevice() {
        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mScanning) {
                    stopDiscovery();
                } else {
                    initDeviceList();
                    doDiscovery();
                }
            }
        });
        mListAdapter = new DeviceListAdapter(this);

        // Find and set up the ListView for paired devices

        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initDeviceList();

        doDiscovery();
    }

    private void initDeviceList() {
        mDeviceList.clear();
        // If there are paired devices, add each one to the ArrayAdapter
        if (mDeviceList.size() > 0) {
            mListAdapter.notifyDataSetChanged();
        }
    }


    // Start device discover with the BluetoothAdapter
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");
        mProgressBar.setVisibility(View.VISIBLE);
        mScanning = true;

        setTitle("Scanning for devices...");

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    @SuppressLint("NewApi")
    private void stopDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mScanning = false;
        mScanButton.setText(R.string.start_scan);
        mProgressBar.setVisibility(View.GONE);
    }

    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //Explain to user why need this permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, R.string.ble_need, Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
            } else {
                scanLeDevice();
            }
        } else {
            scanLeDevice();
        }
    }

    //endregion
    private class DeviceListAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private int listViewResourceId = R.layout.device_name;

        DeviceListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        public int getCount() {
            return mDeviceList.size();
        }

        public Object getItem(int position) {
            return mDeviceList.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View view, ViewGroup parent) {
            View convertView = view;
            try {
                BluetoothDevice device = mDeviceList.get(position);
                //Feed feed = article.getFeed(this.context);
                ViewHolder holder = null;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(listViewResourceId, null);
                    holder.deviceName = convertView.findViewById(android.R.id.text1);
                    holder.deviceAddress = convertView.findViewById(android.R.id.text2);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Boolean isLE = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        isLE = true;
                    }
                }
                String displayName = device.getName() + (isLE ? "(LE)" : "");
                holder.deviceName.setText(displayName);
                holder.deviceAddress.setText(device.getAddress());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        private class ViewHolder {
            TextView deviceName;
            TextView deviceAddress;
        }
    }
}
