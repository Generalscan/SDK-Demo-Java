package com.generalscan.sdkdemo.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.generalscan.scannersdk.core.session.usbhost.service.UsbHostService

class MyService : Service() {

    private val binder = MyServiceBinder()

    override fun onCreate() {
        super.onCreate()
        val mPendingIntent = PendingIntent.getBroadcast(this, 0, Intent("TEST"), 0)
    }
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }
    inner class MyServiceBinder : Binder() {
        val service: MyService
            get() = this@MyService
    }
}