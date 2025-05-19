package com.epson.moverio.moverioarworkflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.epson.moverio.moverioarworkflow.utils.enableImmersiveMode

class TrackpadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trackpad_screen)

        enableImmersiveMode();

    }
}