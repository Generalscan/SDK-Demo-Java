package com.generalscan.sdkdemo.services

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

/**
 * ATServiceConnection 实现ServiceConnection接口，用于连接服务
 *
 * @author Administrator
 */
class MyServiceConnection() : ServiceConnection {

    var service: MyService? = null
        private set
    /**
     * 判断蓝牙是否在连接状态
     * @return
     */
   val isRunning: Boolean
        get() {
            try {
                return service != null
            } catch (e: Exception) {
                return false
            }

        }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val binder = service as MyService.MyServiceBinder
        this.service = binder.service
    }

    override fun onServiceDisconnected(name: ComponentName) {

        // 服务结束绑定
        service!!.stopSelf()
        service = null
    }
}
