package com.epson.moverio.moverioarworkflow.arobject

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epson.moverio.moverioarworkflow.R
import com.maxst.ar.CameraDevice
import com.maxst.ar.ResultCode
import com.maxst.ar.TrackerManager

/**
 * ARObjectExperienceFragment
 *
 * Starts the AR Object Tracking Experience
 * for the BT-40 and BT-45C projects
 *
 *  * Author: Giles Beaumont
 *  * Date: 14/01/2025
 */
class ARObjectExperienceFragment : Fragment() {

    private var glSurfaceView: GLSurfaceView? = null
    private var preferCameraResolution: Int = 0
    private val arObjectExperienceRenderer: ARObjectExperienceRenderer by lazy {
        ARObjectExperienceRenderer(
            requireContext()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ar_object_experience, container, false)

        // Initialize GLSurfaceView
        glSurfaceView = view.findViewById(R.id.gl_surface_view)
        glSurfaceView?.apply {
            setEGLContextClientVersion(2) // Use OpenGL ES 2.0
            setRenderer(arObjectExperienceRenderer)
        }

        //MAXST: Set the images track and the type of tracker as 3D objects
        TrackerManager.getInstance().apply {
            startTracker(TrackerManager.TRACKER_TYPE_OBJECT)
            addTrackerData("ObjectTarget/bt40.3dmap", true)
            addTrackerData("ObjectTarget/bt45c.3dmap", true)
            loadTrackerData()
        }

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
        glSurfaceView!!.onPause()

        //MAXST
        TrackerManager.getInstance().stopTracker()
    }
}