package com.generalscan.sdkdemo.ui.activity.usb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.generalscan.scannersdk.core.basic.interfaces.CommunicateListener;
import com.generalscan.scannersdk.core.basic.interfaces.IConnectSession;
import com.generalscan.scannersdk.core.basic.interfaces.SessionListener;
import com.generalscan.scannersdk.core.pref.UsbHostReferences;
import com.generalscan.scannersdk.core.session.usbhost.basic.UsbHostConsts;
import com.generalscan.scannersdk.core.session.usbhost.connect.UsbHostConnectSession;
import com.generalscan.sdkdemo.R;

import org.jetbrains.annotations.NotNull;


public class ScanBuddyActivity extends AppCompatActivity {

    private static final int TEXT_SIZE = 10;
    private boolean serviceIsOn;
    private UsbHostConnectSession mConnectionSession;

    //region view inject
    private TextView mBtnScan;
    private TextView mBtnStartServcie;
    private TextView mBtnStopService;
    private TextView mTvData;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_scan_buddy);
            bindViews();
            initScanBudyService();
        }
        catch (Exception ex)
        {
            showMessage(ex.getMessage());
            ex.printStackTrace();
        }
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
        mBtnStartServcie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnectionSession.startSession();
            }
        });

        mBtnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnectionSession.endSession();
            }
        });

        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnectionSession.sendData("{a}");
            }
        });


    }
    private void initScanBudyService() {
        UsbHostReferences.Companion.setTriggerMethod(UsbHostConsts.USB_HOST_TRIGGER_MANUAL);
        mConnectionSession = new UsbHostConnectSession(this, false);
        mConnectionSession.setSessionListener(new SessionListener() {
            @Override
            public void onSessionReady(@NotNull IConnectSession iConnectSession) {
                showMessage("USB Session started");

                mConnectionSession.setConnectListener(new CommunicateListener() {
                    //设备断开
                    //Bluetooth device disconnected
                    @Override
                    public void onDisconnected() {
                        showMessage("Device has been disconnected");
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
                        mTvData.append(data);
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
                try
                {
                    mConnectionSession.connect();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }

            @Override
            public void onSessionStartTimeOut(@NotNull IConnectSession iConnectSession) {
                showMessage("USB Session start time out!");
            }
        });
        mConnectionSession.startSession();
    }

    private void showMessage(int messageResId) {
        showMessage(getText(messageResId));
    }

    private void showMessage(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
