package com.epson.moverio.moverioarworkflow

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.epson.moverio.btcontrol.UIControl
import com.epson.moverio.moverioarworkflow.utils.enableImmersiveMode
import com.epson.moverio.system.DeviceManager

class SplashActivity : AppCompatActivity() {

    private var mUiMode: UIControl? = null
    private var mDeviceManager: DeviceManager? = null

    companion object {
        private const val SPLASH_TIME_OUT: Long = 3000 // 3 seconds
        private const val TRACKPAD_MODE_MESSAGE =
            "AR feature requires Trackpad mode\nPlease re-open app"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        //Epson: Start background service to listen if the Moverio headset is connected or Disconnected
        val intent = Intent(this, HeadsetService::class.java)
        startService(intent)

        //Epson: Call utility class run app in fullscreen
        enableImmersiveMode()

        mDeviceManager = DeviceManager(this).apply {
            if (!isHeadsetAttached) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        //Epson: Check if UI Mode is Trackpad mode,
        //mUiMode = UIControl(this)
        Handler(Looper.getMainLooper()).postDelayed({

//                mUiMode?.let {
//                    if (it.uiMode == UIControl.UI_MODE_MIRROR) {
//                        Toast.makeText(this, TRACKPAD_MODE_MESSAGE, Toast.LENGTH_SHORT).show()
//                    }
//                }

            //Epson: If the Moverio headset is connected then go to main activity, else show an info page
            mDeviceManager?.let {
                if (!it.isHeadsetAttached) {
                    val intent = Intent(this, TrackpadActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            finish()
        }, SPLASH_TIME_OUT)
    }
}
