package com.generalscan.sdkdemo.ui.activity.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.generalscan.scannersdk.core.basic.SdkContext;
import com.generalscan.sdkdemo.R;
import com.generalscan.sdkdemo.ui.activity.bluetooth.BluetoothMainActivity;
import com.generalscan.sdkdemo.ui.activity.usb.ScanBuddyActivity;
import com.generalscan.sdkdemo.ui.activity.usb.UsbHostActivity;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_CONNECT_BLUETOOTH_DEVICE = 10;
    private static final int REQUEST_START_USBHOST_SERIVCE = 20;
    private static final int REQUEST_USER_PERMISSION = 30;

    private boolean mHasDestroy;
    private final String[] PERMISSIONS = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SdkContext.INSTANCE.initSdk(this, null);
        //Request Permission
        Boolean requestPermission = shouldRequestPermissions(this, PERMISSIONS);
        if (requestPermission) {
            Boolean shouldShowExplanation = shouldShowRequestPermissionRationale(this, PERMISSIONS);
            if (shouldShowExplanation) {
                Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Permission Need");
                builder.setMessage("This app requires permission to access SDCard and connect bluetooth scanner before it can start.\n\nPlease press \"Allow\" on the permission window appears.");

                builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat
                                .requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_USER_PERMISSION);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_USER_PERMISSION);
            }
        }
        findViewById(R.id.button_bluetooth_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothMainActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_BLUETOOTH_DEVICE);
            }
        });

        findViewById(R.id.button_usb_host).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, ScanBuddyActivity.class);
                //startActivityForResult(intent, REQUEST_CONNECT_BLUETOOTH_DEVICE);
                Toast.makeText(MainActivity.this, "Please plug in the ScanBuddy device to the phone", Toast.LENGTH_LONG).show();
            }
        });
    }


    /*--- Permission check function -----*/
    private Boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private Boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions){
        if (Build.VERSION.SDK_INT < 23) { //Android M
            return false;
        }

        Boolean requestPermission = false;
        for (String permission : permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                requestPermission = true;
                break;
            }
        }
        return requestPermission;
    }

    private Boolean shouldRequestPermissions(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT < 23) { //Android M
            return false;
        }

        Boolean requestPermission = false;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermission = true;
                break;
            }
        }

        return requestPermission;
    }
}
