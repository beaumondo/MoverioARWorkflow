package com.epson.moverio.moverioarworkflow.arimage

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epson.moverio.moverioarworkflow.R
import com.epson.moverio.moverioarworkflow.arstandard.StandardEnd
import com.maxst.ar.CameraDevice
import com.maxst.ar.ResultCode
import com.maxst.ar.TrackerManager

/**
 * ARImageExperienceFragment
 *
 * Starts the AR Image Tracking Experience
 *
 *  * Author: Giles Edward Beaumont
 *  * Date: 31/12/2024
 */
class ARImageExperienceFragment : Fragment() {

    private var glSurfaceView: GLSurfaceView? = null
    private var preferCameraResolution: Int = 0
    private var arImageExperienceRenderer: ARImageExperienceRenderer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ar_image_experience, container, false)

        arImageExperienceRenderer = ARImageExperienceRenderer(requireContext())

        // Initialize GLSurfaceView
        glSurfaceView = view.findViewById(R.id.gl_surface_view)
        glSurfaceView?.apply {
            setEGLContextClientVersion(2) // Use OpenGL ES 2.0
            setRenderer(arImageExperienceRenderer)
        }

        //MAXST: Set the images track and the type of tracker
        TrackerManager.getInstance().apply {
            startTracker(TrackerManager.TRACKER_TYPE_IMAGE)
            addTrackerData("ImageTarget/bt40.2dmap", true)
            addTrackerData("ImageTarget/bt45c.2dmap", true)
            loadTrackerData()
        }

        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val nextStepButton = view.findViewById<Button>(R.id.btnNext)
        nextStepButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, StandardEnd())
                    .addToBackStack("fragment_ar_end")
                    .commit()
        }

        val previousARButton = view.findViewById<ImageButton>(R.id.imgBtnLeft)
        previousARButton.setOnClickListener {
            arImageExperienceRenderer?.moveToPreviousStep() ?: return@setOnClickListener
        }

        val nextARButton = view.findViewById<ImageButton>(R.id.imgBtnRight)
        nextARButton.setOnClickListener {
            arImageExperienceRenderer?.moveToNextStep() ?: return@setOnClickListener
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onResume() {
        super.onResume()

        glSurfaceView!!.onResume()

        //MAXST
        TrackerManager.getInstance().startTracker(TrackerManager.TRACKER_TYPE_IMAGE)

        val resultCode = when (preferCameraResolution) {
            0 -> CameraDevice.getInstance().start(0, 640, 480)
            1 -> CameraDevice.getInstance().start(0, 1280, 720)
            2 -> CameraDevice.getInstance().start(0, 1920, 1080)
            else -> ResultCode.Success
        }

        if (resultCode != ResultCode.Success) {
            Toast.makeText(requireContext(), R.string.camera_open_fail, Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()

        //MAXST
        TrackerManager.getInstance().stopTracker()

    }
}


