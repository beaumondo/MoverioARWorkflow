package com.epson.moverio.moverioarworkflow

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.epson.moverio.hardware.display.DisplayManager
import com.epson.moverio.moverioarworkflow.utils.enableImmersiveMode
import com.epson.moverio.util.PermissionGrantResultCallback
import com.epson.moverio.util.PermissionHelper
import com.google.android.material.slider.Slider
import java.io.IOException


class AdjustmentActivity : AppCompatActivity() {

    private lateinit var mDisplayManager: DisplayManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjustment)

        //Epson: Call utility class run app in fullscreen
        enableImmersiveMode()

        val slider = findViewById<Slider>(R.id.sliderDistance)
        val backButton = findViewById<Button>(R.id.btnBack)

        mDisplayManager = DisplayManager(this, object : PermissionGrantResultCallback {
            override fun onPermissionGrantResult(permission: String, grantResult: Int) {
                if (grantResult == PermissionHelper.PERMISSION_GRANTED) {
                    Log.d("AdjustmentActivity", "Permission granted: $permission")
                } else {
                    Log.e("AdjustmentActivity", "Permission denied: $permission")
                }
            }
        })

        // Open the display manager
        try {
            mDisplayManager.open()
        } catch (e: IOException) {
            Log.e("AdjustmentActivity", "Failed to open DisplayManager", e)
        }

        backButton.setOnClickListener {
            //onBackPressed()
            finish()
        }

        slider.addOnChangeListener { _, value, _ ->
            // Value is the current slider position
            val adjustedDistance = value.toInt()

            mDisplayManager.setScreenHorizontalShiftStep(adjustedDistance)

            val result = mDisplayManager.setScreenHorizontalShiftStep(adjustedDistance)
            if (result == 0) {
                Log.d("AdjustmentActivity", "Horizontal shift set to: $adjustedDistance")
            } else {
                Log.e("AdjustmentActivity", "Failed to set horizontal shift: $adjustedDistance")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // Close the DisplayManager to release hardware resources
            mDisplayManager.close()
        } catch (e: IOException) {
            Log.e("AdjustmentActivity", "Failed to close DisplayManager", e)
        } finally {
            // Release any additional resources
            mDisplayManager.release()
        }
    }
}