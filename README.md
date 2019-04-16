# SDK-Demo-Java

This is a demo project for Generalscan scanner SDK



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