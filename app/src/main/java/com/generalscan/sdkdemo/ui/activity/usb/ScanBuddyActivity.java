package com.generalscan.sdkdemo.ui.activity.usb;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.generalscan.scannersdk.core.basic.SdkContext;
import com.generalscan.scannersdk.core.basic.consts.SdkReceiveConfig;
import com.generalscan.scannersdk.core.basic.interfaces.CommunicateListener;
import com.generalscan.scannersdk.core.basic.interfaces.IConnectSession;
import com.generalscan.scannersdk.core.basic.interfaces.SessionListener;
import com.generalscan.scannersdk.core.pref.UsbHostReferences;
import com.generalscan.scannersdk.core.session.usbhost.basic.UsbHostConsts;
import com.generalscan.scannersdk.core.session.usbhost.connect.UsbHostConnectSession;
import com.generalscan.sdkdemo.R;
import com.generalscan.sdkdemo.Utils.DeviceLogger;

import org.jetbrains.annotations.NotNull;


public class ScanBuddyActivity extends AppCompatActivity {

    private static final int TEXT_SIZE = 10;
    private boolean serviceIsOn;
    private UsbHostConnectSession mConnectionSession;

    //region view inject
    private TextView mBtnScan;
    private TextView mBtnStartServcie;
    private TextView mBtnStopService;
    private TextView mBtnConnect;
    private TextView mBtnDisconnect;
    private TextView mTvData;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_scan_buddy);
            bindViews();
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);

            initUsbConnection();
        }
        catch (Exception ex)
        {
            showMessage(ex.getMessage());
            ex.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        restartUsbService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            if (mConnectionSession!= null) {
                mConnectionSession.endSession();
            }
        }
        catch (Exception ex)
        {
            showMessage(ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void bindViews() {
        mBtnScan = findViewById(R.id.button_scan);
        mBtnStartServcie = findViewById(R.id.button_start_service);
        mBtnStopService = findViewById(R.id.button_stop_service);
        mTvData = findViewById(R.id.textview_data);

        //mTvData.setText("Please press the START SCAN button to trigger scan");
        mTvData.setMovementMethod(new ScrollingMovementMethod());
        mBtnStartServcie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    mConnectionSession.endSession();
                    mConnectionSession.startSession();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }

            }
        });

        mBtnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    mConnectionSession.endSession();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        });

        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    mTvData.requestFocus();
                    mConnectionSession.sendData("{a}");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }

            }
        });


    }
    private void restartUsbService() {
        try {
            mBtnScan.setVisibility(View.GONE);
            if (mConnectionSession != null)
                mConnectionSession.endSession();
            mConnectionSession.startSession();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

    private void initUsbConnection() {
        SdkContext.INSTANCE.initSdk(this, new DeviceLogger(this));
        SdkReceiveConfig.INSTANCE.setCurrentCharsetCode(SdkReceiveConfig.CHARSET_UTF8);
        UsbHostReferences.Companion.setTriggerMethod(UsbHostConsts.USB_HOST_TRIGGER_MANUAL);
        mConnectionSession = new UsbHostConnectSession(this, false);
        mConnectionSession.setSessionListener(new SessionListener() {
            @Override
            public void onSessionReady(@NotNull IConnectSession iConnectSession) {
                //showMessage("USB Session started");
                mConnectionSession.setConnectListener(new CommunicateListener() {
                    //设备断开
                    //Bluetooth device disconnected
                    @Override
                    public void onDisconnected() {
                        showMessage("Device has been disconnected");
                        ScanBuddyActivity.this.finish();
                    }

                    //设备连接失败
                    //Bluetooth device connect failed
                    @Override
                    public void onConnectFailure(String errorMessage) {
                        showMessage(errorMessage);
                    }

                    //设备连接成功
                    //Bluetooth device connect success
                    @Override
                    public void onConnected() {
                        showMessage(R.string.scanner_connect_success);
                    }

                    //接收到扫描器数据
                    //Scanner data received
                    @Override
                    public void onDataReceived(String data) {
                        String txtViewData = mTvData.getText().toString();
                        int count = 0;
                        if(txtViewData.contains("\n"))
                           count = txtViewData.split("\n").length + 1;
                        else
                           count = 1;
                        mTvData.append(String.valueOf(count) + ": " + data);
                    }

                    //命令返回数据
                    //Bluetooth command callback
                    @Override
                    public void onCommandCallback(String name, String data) {

                    }

                    //电池数据接收
                    //Battery data receive
                    @Override
                    public void onBatteryDataReceived(String voltage, String percentage) {

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

                try {
                    mConnectionSession.connect();
                    mBtnScan.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }

            @Override
            public void onSessionStartTimeOut(@NotNull IConnectSession iConnectSession) {
                showMessage("USB Session start time out!");
            }
        });

    }

    private void showMessage(int messageResId) {
        showMessage(getText(messageResId));
    }

    private void showMessage(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
