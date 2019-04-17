package com.generalscan.sdkdemo.ui.activity.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.generalscan.scannersdk.core.basic.consts.SdkReceiveConfig;
import com.generalscan.scannersdk.core.basic.interfaces.IConnectSession;
import com.generalscan.scannersdk.core.basic.interfaces.SessionListener;
import com.generalscan.scannersdk.core.pref.SDKGlobalReferences;
import com.generalscan.sdkdemo.R;
import com.generalscan.scannersdk.core.basic.interfaces.BluetoothPairListener;
import com.generalscan.scannersdk.core.basic.interfaces.CommunicateListener;
import com.generalscan.scannersdk.core.session.bluetooth.BluetoothSettings;
import com.generalscan.scannersdk.core.session.bluetooth.connect.BluetoothConnectSession;
import com.generalscan.scannersdk.core.session.bluetooth.utils.BluetoothPairCtl;

import org.jetbrains.annotations.NotNull;


public class BluetoothMainActivity extends AppCompatActivity {
    private final int REQUEST_SELECT_BLUETOOTH_DEVICE = 10;
    private final int REQUEST_BLUETOOTH_SETTINGS = 20;
    //region variables
    protected BluetoothAdapter mBluetoothAdapter;

    //Controller for Bluetooth Pairing
    private BluetoothPairCtl mPairController;
    //蓝牙连接任务
    //Bluetooth Connect Session
    private BluetoothConnectSession mBluetoothConnectSession;
    private String mSelectedDeviceAddress;
    //endregion

    //region view widgents
    private TextView mBtnTurnBluetooth;
    private TextView mBtnSelectDevice;
    private ViewGroup mLayConnect;
    private ViewGroup mLaySetting;
    private Button mBtnSendContent;
    private EditText mTxtReceiveData;
    private EditText mTxtCommand;


    //endregion
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_main);

        //region Bind Views
        mBtnTurnBluetooth = findViewById(R.id.btnTurnBluetooth);
        mBtnSelectDevice = findViewById(R.id.btnSelectDevice);
        mLayConnect = findViewById(R.id.layConnect);
        mLaySetting = findViewById(R.id.laySetting);
        mBtnSendContent = findViewById(R.id.btnSendContent);
        mTxtReceiveData = findViewById(R.id.txtReceiveData);
        mTxtCommand = findViewById(R.id.txtCommand);
        //endregion

        mLayConnect.setVisibility(View.VISIBLE);
        mLaySetting.setVisibility(View.GONE);
        initBluetooth(); //Initial bluetooth environment
        updateBluetoothStatus();//Update bluetooth status
        setListener();//Set listeners
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //选择设备后
            //After select bluetooth device
            case REQUEST_SELECT_BLUETOOTH_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedDeviceAddress = data.getStringExtra("Address");
                    pairDevice(false);
                } else {

                    mBtnSelectDevice.setEnabled(true);
                }
                break;
            //开启蓝牙后
            //After bluetooth turns on
            case REQUEST_BLUETOOTH_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    updateBluetoothStatus();
                }
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //Release pair controller
            //释放Pair Controller
            mPairController.releaseRegister();
            //结束当前蓝牙任务
            //Send current bluetooth session
            mBluetoothConnectSession.endSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化蓝牙环境
    //Initial bluetooth environment
    private void initBluetooth() {
        //新建蓝牙连接任务
        //Create bluetooth connect session
        mBluetoothConnectSession = new BluetoothConnectSession(this);
        mBluetoothConnectSession.setSessionListener(new SessionListener() {
            @Override
            public void onSessionReady(IConnectSession iConnectSession) {
                mBluetoothConnectSession.setConnectListener(new CommunicateListener() {
                    //蓝牙设备断开
                    //Bluetooth device disconnected
                    @Override
                    public void onDisconnected() {
                        showMessage("Device has been disconnected");
                    }

                    //蓝牙设备连接失败
                    //Bluetooth device connect failed
                    @Override
                    public void onConnectFailure(String errorMessage) {
                        mBtnSelectDevice.setEnabled(true);
                        showMessage(errorMessage);
                    }

                    //蓝牙设备连接成功
                    //Bluetooth device connect success
                    @Override
                    public void onConnected() {
                        showMessage(R.string.scanner_connect_success);
                        mBtnSelectDevice.setEnabled(false);
                        mLayConnect.setVisibility(View.GONE);
                        mLaySetting.setVisibility(View.VISIBLE);
                    }

                    //接收到扫描器数据
                    //Scanner data received
                    @Override
                    public void onDataReceived(String data) {
                        mTxtReceiveData.append(data);
                        final ScrollView scrollView = (ScrollView) mTxtReceiveData.getParent();
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }

                    //蓝牙命令返回数据
                    //Bluetooth command callback
                    @Override
                    public void onCommandCallback(String name, String data) {
                        mTxtReceiveData.append("$name:$data");
                        final ScrollView scrollView = (ScrollView) mTxtReceiveData.getParent();
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }

                    //电池数据接收
                    //Battery data receive
                    @Override
                    public void onBatteryDataReceived(String voltage, String percentage) {
                        mTxtReceiveData.append(voltage + ":" + percentage);
                    }

                    //扫描器命令超时
                    //Scanner command timeout
                    @Override
                    public void onCommandNoResponse(String errorMessage) {

                    }

                    //数据接收错误
                    //Data receive error
                    @Override
                    public void onRawDataReceiveError(String errorMessage, String source) {

                    }

                    //原始数据接收
                    //Raw data receive
                    @Override
                    public void onRawDataReceived(byte data) {

                    }
                });
            }

            @Override
            public void onSessionStartTimeOut(@NotNull IConnectSession iConnectSession) {
                showMessage("Session Timeout");
            }
        });
        mBluetoothConnectSession.startSession();
        //指定编码方式以提高输出速度
        // Speed up output
        SdkReceiveConfig.INSTANCE.setCurrentCharsetCode(SdkReceiveConfig.CHARSET_UTF8);

    }

    //匹配蓝牙设备
    //Pair bluetooth device
    private void pairDevice(Boolean deviceDiscovery) {
        if (mPairController == null) {
            mPairController = new BluetoothPairCtl(this, new PairListener());
        }
        //We do not provide PIN because as may change PIN in some of the cases
        mPairController.tryPairDevice(mSelectedDeviceAddress, "");
    }

    //连接蓝牙设备
    //Connect bluetooth device
    private void connectDevice() {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mSelectedDeviceAddress);
        mBluetoothConnectSession.setBluetoothDeviceToConnect(device);
        mBluetoothConnectSession.connect();
    }

    //刷新蓝牙状态
    //Refresh bluetooth status
    private void updateBluetoothStatus() {

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetoothAdapter.isEnabled()) {
            mBtnTurnBluetooth.setEnabled(false);
            mBtnTurnBluetooth.setText(R.string.B_HadTurnOn);
            mBtnSelectDevice.setEnabled(true);

        } else {
            mBtnSelectDevice.setEnabled(false);
        }


    }

    //设置监听器
    //Set listener
    private void setListener() {
        // 启用蓝牙
        // Turn on bluetooth
        mBtnTurnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_SETTINGS);
            }
        });


        //选择蓝牙设备
        //Select bluetooth device
        mBtnSelectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnSelectDevice.setEnabled(false);
                Intent intent = new Intent(BluetoothMainActivity.this, BluetoothDeviceListActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_BLUETOOTH_DEVICE);
            }
        });


        // 发送命令
        // Send command
        mBtnSendContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mTxtCommand.getText().toString();
                mTxtReceiveData.requestFocus();
                mBluetoothConnectSession.sendData(text);
            }
        });
    }

    private void showMessage(int messageResId) {
        showMessage(getText(messageResId));
    }

    private void showMessage(CharSequence message) {
        Toast.makeText(BluetoothMainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private class PairListener implements BluetoothPairListener {

        @Override
        public void onPairSuccess(boolean isBle) {
            connectDevice();
        }

        @Override
        public void onRequestPin() {
            int i = 1;
        }

        @Override
        public void onFailure(String errorMessage) {
            mBtnSelectDevice.setEnabled(true);
            showMessage(errorMessage);
        }
    }
}
