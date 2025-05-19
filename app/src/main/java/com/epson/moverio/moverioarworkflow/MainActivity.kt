package com.epson.moverio.moverioarworkflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.maxst.ar.MaxstAR


class MainActivity : AppCompatActivity() {

    // Register the permission request launcher
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted
                Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(
                    this,
                    "Camera permission is required to use this feature.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set full-screen immersive mode for BT-45CS
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        // Check and request camera permission
        checkAndRequestCameraPermission()

        // Initialize MAXST AR
        MaxstAR.init(getApplicationContext(), BuildConfig.MAXST_LICENSE_KEY);
        MaxstAR.setScreenOrientation(getResources().getConfiguration().orientation);


        // Check if the fragment is already in the container (to avoid recreating it on config changes)
        if (savedInstanceState == null) {
            // Create an instance of the fragment
            val mainFragment = MainFragment()

            // Load the fragment into the container
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainerView,
                    mainFragment
                ) // Use replace to ensure it's loaded
                .commit()
        }
    }

    private fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted
                Toast.makeText(this, "Camera permission is already granted.", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                // Request permission
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}