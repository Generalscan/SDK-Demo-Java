# SDK-Demo-Java

This is a demo project for Generalscan scanner SDK


## Download SDK aar

Download from here : [SDK aar](download/sdk-aar.zip)

## Import SDK into android studio project
1) Copy the SDK aar file (let say generalscan-sdk-1.0.aar) to app/libs
2) Add the flatDir setting Gradlee configuration to your Android project. In your root `build.gradle` file:
```groovy
allprojects { 
	repositories 
		{ 
			jcenter() 
			flatDir { dirs 'libs' }  // add flatDir setting
		} 
}
```


3) Open app level build.grdle file and add .aar file and kotlin runtime
```groovy
dependencies 
{ 
	implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.3.10' 
	implementation(name:'generalscan-sdk-1.0', ext:'aar') 
}
```


4) Add the follow permissions into manifest.xml
```xml
<!-- Permission For Bluetooth -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.VIBRATE" />

<!-- Permissions for USB Host (Scan Buddy) -->
<uses-feature android:name="android.hardware.usb.host" android:required="true" />
<!-- Show Alert Dialog in Service -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```


5) Add the follow service declarations into manifest.xml
```xml
<service android:name="com.generalscan.scannersdk.core.session.bluetooth.service.BluetoothConnectService"
    android:enabled="true"
    android:exported="true" />

<service android:name="com.generalscan.scannersdk.core.session.usbhost.service.UsbHostService"
    android:enabled="true"
    android:exported="true" />

<service android:name="com.generalscan.scannersdk.core.session.usbhost.service.FloatingScanButtonService"
    android:enabled="true"
    android:exported="true" />
```


# Work with bluetooth scanner
1) Turn on Bluetooth
2) Start Bluetooth session after activity created
```java
BluetoothConnectSession mBluetoothConnectSession = new BluetoothConnectSession(this);
//Setup session listener	
//设置 Session 监听
mBluetoothConnectSession.setSessionListener(new SessionListener()	
{
	//When session is ready
	@Override
	public void onSessionReady(IConnectSession iConnectSession) {

		//TODO:to connect the device here or later
	}

	//When session service initialization timeout
	@Override
	public void onSessionStartTimeOut(IConnectSession iConnectSession) {
		//TODO:show error message
		}
	}
);

mBluetoothConnectSession.startSession();
```

3) Setup listener to receive data
```java
mBluetoothConnectSession.setConnectListener(
	new CommunicateListener()
	{
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
			showMessage(errorMessage);
		}
		
		//蓝牙设备连接成功
		//Bluetooth device connect success
		@Override
		public void onConnected() { 
			showMessage(R.string.scanner_connect_success); 
		}
		
		//接收到扫描器数据
		//Scanner data received
		@Override
		public void onDataReceived(String data) { 
			mTxtReceiveData.append(data);
		}
		
		//蓝牙命令返回数据
		//Bluetooth command callback
		@Override
		public void onCommandCallback(String name, String data) {
			mTxtReceiveData.append("$name:$data"); 
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
	}
);
```

4) Pair Bluetooth device

5) Connect Bluetooth device
```java
BluetoothAdapter mBluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mSelectedDeviceAddress); 
mBluetoothConnectSession.setBluetoothDeviceToConnect(device);
mBluetoothConnectSession.connect();
```

6) Stop Bluetooth session after activity is destroy
```java
//Send current bluetooth session
mBluetoothConnectSession.endSession();
```

