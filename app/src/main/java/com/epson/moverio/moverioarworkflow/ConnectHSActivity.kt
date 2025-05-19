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


class ConnectHSActivity : AppCompatActivity() {
    private var mUiMode: UIControl? = null
    private var mDeviceManager: DeviceManager? = null

    companion object {
        private const val TRACKPAD_MODE_MESSAGE =
            "AR feature requires Trackpad mode\nPlease re-open app"
        private const val SPLASH_TIME_OUT: Long = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mUiMode = UIControl(this)
        mDeviceManager = DeviceManager(this)

        enableImmersiveMode();

        mDeviceManager?.let {
            if (!it.isHeadsetAttached) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }


        Handler(Looper.getMainLooper()).postDelayed({
//                mUiMode?.let {
//                    if (it.uiMode == UIControl.UI_MODE_MIRROR) {
//                        Toast.makeText(this, TRACKPAD_MODE_MESSAGE, Toast.LENGTH_SHORT).show()
//                        it.setUiMode(UIControl.UI_MODE_TRACK)
//                    }
//                }

            mDeviceManager?.let {
                if (!it.isHeadsetAttached) {
                    val intent = Intent(this, DetachedActivity::class.java)
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                }
            }

            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)
    }
}