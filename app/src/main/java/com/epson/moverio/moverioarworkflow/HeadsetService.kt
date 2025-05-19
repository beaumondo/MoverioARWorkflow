package com.epson.moverio.moverioarworkflow

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.epson.moverio.system.DeviceManager
import com.epson.moverio.system.HeadsetStateCallback

//Epson: Service that runs in the background and listens for changes in Moverio system statu.
class HeadsetService : Service(), HeadsetStateCallback {

    private val TAG = "Epson";

    //Epson: This class manage the temperature, system status of the Moverio headset.
    private var mDeviceManager: DeviceManager? = null

    // Binder to allow activities to interact with the service, if needed
    private val binder = LocalBinder()

    //Epson TODO: Check binder implementation
    inner class LocalBinder : Binder() {
        fun getService(): HeadsetService = this@HeadsetService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    //Epson: Callback method for when the Moverio headset is attached to the USB port
    override fun onHeadsetAttached() {
        Log.e(TAG, "Headset Attached")
    }

    //Epson: Callback method for when the Moverio headset is detached from the USB port
    override fun onHeadsetDetached() {
        Log.w(TAG, "Moverio Headset Detached")
        val intent = Intent(this, DetachedActivity::class.java)
        startActivity(intent)

    }

    //Epson: Callback method for when the Moverio headset is Displaying Content
    override fun onHeadsetDisplaying() {
        Log.i(TAG, "Moverio Headset is Displaying")
    }

    //Epson: Callback method for Managing temperature error
    override fun onHeadsetTemperatureError() {
        Log.e(TAG, "Moverio Temperature abnormality")
    }


}

