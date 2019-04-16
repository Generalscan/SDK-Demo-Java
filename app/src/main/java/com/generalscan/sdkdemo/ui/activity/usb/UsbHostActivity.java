package com.generalscan.sdkdemo.ui.activity.usb;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.generalscan.sdkdemo.R;
import com.generalscan.scannersdk.core.basic.interfaces.CommunicateListener;
import com.generalscan.scannersdk.core.pref.UsbHostReferences;
import com.generalscan.scannersdk.core.session.usbhost.basic.UsbHostConsts;
import com.generalscan.scannersdk.core.session.usbhost.connect.UsbHostConnectSession;
import com.generalscan.sdkdemo.Utils.AsyncTask2;
import com.generalscan.sdkdemo.Utils.AsyncTaskCall;
import com.generalscan.sdkdemo.Utils.AsyncTaskCallback;
import com.generalscan.sdkdemo.Utils.CallResult;



public class UsbHostActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int TEXT_SIZE = 10;
    private boolean serviceIsOn;
    private UsbHostConnectSession mConnectionSession;

    //region view inject
    private CheckBox mCkbVibrate;
    private EditText mTxtVibrateTime;
    private TextView mBtnSaveSettings;
    private Button mBtnStartServcie;
    private Button mBtnStopService;
    private SeekBar mSeekBar;// 透明度调整
    private SeekBar mSeekBarFont;// 字体大小
    private TextView mTvFont;// 测试字体
    private RadioButton mRbtnStyle1;// 样式选择
    private RadioButton mRbtnStyle2;
    private RadioButton mRbtnStyle3;
    private RadioGroup radioTriggerMethods;
    private RadioButton mRadioTriggerByGravity;
    private RadioButton mRadioTriggerByButton;
    private TextView mTvData;
    private View mLayTriggerButtonSettings;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_host);
        bindViews();
        mLayTriggerButtonSettings.setVisibility(View.GONE);
        mConnectionSession = new UsbHostConnectSession(this, true);
        bindViews();
        readSettings();
        saveAndRestartService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnectionSession.getFloatingScanButtonService() != null) {
            mConnectionSession.endSession();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (mConnectionSession.getFloatingScanButtonService() != null) {

            if (mRbtnStyle1.isChecked()) {
                //按钮样式1
                mConnectionSession.getFloatingScanButtonService().setBtnBg(1);
                UsbHostReferences.Companion.setTriggerButtonBackground(1);
            } else if (mRbtnStyle2.isChecked()) {
                //按钮样式2
                mConnectionSession.getFloatingScanButtonService().setBtnBg(2);
                UsbHostReferences.Companion.setTriggerButtonBackground(2);
            } else if (mRbtnStyle3.isChecked()) {
                //按钮样式3
                mConnectionSession.getFloatingScanButtonService().setBtnBg(3);
                UsbHostReferences.Companion.setTriggerButtonBackground(3);
            }
        }
    }

    private void bindViews() {
        mCkbVibrate = findViewById(R.id.checkbox_vibrate);
        mTxtVibrateTime = findViewById(R.id.edittext_vibrate_time);
        mBtnSaveSettings = findViewById(R.id.button_save_settings);
        mBtnStartServcie = findViewById(R.id.button_start_service);
        mBtnStopService = findViewById(R.id.button_stop_service);
        mSeekBar = findViewById(R.id.seekbar_transparency);
        mSeekBarFont = findViewById(R.id.seekbar_font_size);
        mTvFont = findViewById(R.id.textview_test_font);
        mRbtnStyle1 = findViewById(R.id.radiobutton_style_1);
        mRbtnStyle2 = findViewById(R.id.radiobutton_style_2);
        mRbtnStyle3 = findViewById(R.id.radiobutton_style_3);
        radioTriggerMethods = findViewById(R.id.radiogroup_trigger_methods);
        mRadioTriggerByGravity = findViewById(R.id.radiobutton_trigger_by_gravity);
        mRadioTriggerByButton = findViewById(R.id.radiobutton_trigger_by_button);
        mTvData = findViewById(R.id.textview_data);
        mLayTriggerButtonSettings = findViewById(R.id.layout_trigger_button_settings);

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

        mBtnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mConnectionSession.getFloatingScanButtonService() != null) {
                    // 设置透明度
                    mConnectionSession.getFloatingScanButtonService().setAlpha(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mSeekBarFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTvFont.setTextSize(i + TEXT_SIZE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // 设置样式选择
        mRbtnStyle1.setOnCheckedChangeListener(this);
        mRbtnStyle2.setOnCheckedChangeListener(this);
        mRbtnStyle3.setOnCheckedChangeListener(this);

        radioTriggerMethods.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radiobutton_trigger_by_button) {
                    mRadioTriggerByButton.setChecked(true);
                    mLayTriggerButtonSettings.setVisibility(View.VISIBLE);
                } else {
                    mRadioTriggerByGravity.setChecked(true);
                    mLayTriggerButtonSettings.setVisibility(View.GONE);
                }
                saveAndRestartService();
            }
        });
        setScreen();
    }


    private void saveAndRestartService() {
        saveSettings();
        //End current session
        mConnectionSession.endSession();
        //Start new session
        mConnectionSession.startSession();
        if (mConnectionSession.getFloatingScanButtonService() != null) {
            mConnectionSession.getFloatingScanButtonService().onTriggerMethodChange();
        }
        final long startTime = System.currentTimeMillis();
        new AsyncTask2(new AsyncTaskCall() {
            @Override
            public Context getContext() {
                return UsbHostActivity.this;
            }

            @Override
            public CallResult start() {
                CallResult result = new CallResult();
                Boolean serviceConnected = false;
                while (System.currentTimeMillis() - startTime < 5000 && !serviceConnected) {
                    if (mConnectionSession.getFloatingScanButtonService() != null) {
                        serviceConnected = true;
                        break;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {

                    }
                }
                if (!serviceConnected) result.setSuccess(false);
                return result;
            }
        }, new AsyncTaskCallback() {
            @Override
            public Context getContext() {
                return UsbHostActivity.this;
            }

            @Override
            public void hasFinished(CallResult result) {
                if (result.isSuccess()) {
                    mConnectionSession.connect();
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
                } else {
                    Toast.makeText(getContext(), getText(R.string.usb_host_service_not_start), Toast.LENGTH_LONG).show();
                }
            }


        }, null).execute();

    }


    /**
     * 设置屏幕转屏
     */
    private void setScreen() {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
    }

    private void readSettings() {

        if (UsbHostReferences.Companion.getTriggerMethod() == UsbHostConsts.USB_HOST_TRIGGER_METHOD_BUTTON) {
            mRadioTriggerByButton.setChecked(true);
            mLayTriggerButtonSettings.setVisibility(View.VISIBLE);
        } else {
            mRadioTriggerByGravity.setChecked(true);
            mLayTriggerButtonSettings.setVisibility(View.GONE);
        }


        mCkbVibrate.setChecked(UsbHostReferences.Companion.isVibrate());

        // 设置透明度
        mSeekBar.setProgress(UsbHostReferences.Companion.getTriggerButtonAlpha());
        mSeekBarFont.setProgress(UsbHostReferences.Companion.getTriggerButtonTextSize());
        mTvFont.setTextSize(UsbHostReferences.Companion.getTriggerButtonTextSize() + TEXT_SIZE);
        mTxtVibrateTime.setText(String.valueOf(UsbHostReferences.Companion.getVibrateTime()));

        // 显示默认样式
        switch(UsbHostReferences.Companion.getTriggerButtonBackground())
        {
            case 1 :
                mRbtnStyle1.setChecked(true);
                break;
            case 2 :
                mRbtnStyle2.setChecked(true);
                break;
            case 3 :
                mRbtnStyle3.setChecked(true);
                break;
        }
    }

    private void saveSettings() {
        if (mRadioTriggerByButton.isChecked()) {
            UsbHostReferences.Companion.setTriggerMethod(UsbHostConsts.USB_HOST_TRIGGER_METHOD_BUTTON);
        }

        if (mRadioTriggerByGravity.isChecked()) {
            UsbHostReferences.Companion.setTriggerMethod(UsbHostConsts.USB_HOST_TRIGGER_METHOD_GRAVITY);
        }

        UsbHostReferences.Companion.setVibrate(mCkbVibrate.isChecked());

        UsbHostReferences.Companion.setTriggerButtonAlpha(mSeekBar.getProgress());
        UsbHostReferences.Companion.setTriggerButtonTextSize(mSeekBarFont.getProgress());
        if(mTxtVibrateTime.getText().toString().isEmpty())
            UsbHostReferences.Companion.setVibrateTime(50);
        else
            UsbHostReferences.Companion.setVibrateTime(Long.valueOf(mTxtVibrateTime.getText().toString()));

    }

    private void showMessage(int messageResId) {
        showMessage(getText(messageResId));
    }

    private void showMessage(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
