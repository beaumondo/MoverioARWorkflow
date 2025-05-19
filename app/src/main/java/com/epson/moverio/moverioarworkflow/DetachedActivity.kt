package com.epson.moverio.moverioarworkflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.epson.moverio.moverioarworkflow.utils.enableImmersiveMode

/**
 * This activity is displayed when the headset is detached.
 */
class DetachedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detached_screen)
        enableImmersiveMode();


    }
}