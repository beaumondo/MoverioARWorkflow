package com.epson.moverio.moverioarworkflow.arobject

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.epson.moverio.moverioarworkflow.renderer.BackgroundRenderHelper
import com.epson.moverio.moverioarworkflow.renderer.TextRenderer
import com.maxst.ar.CameraDevice
import com.maxst.ar.MaxstAR
import com.maxst.ar.TrackedImage
import com.maxst.ar.TrackerManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * ARObjectExperienceRenderer
 *
 * Renders content for either the BT-45C or BT-40 depending
 * on the tracked object.
 *
 *  * Author: Giles Beaumont
 *  * Date: 31/12/2024
 */
class ARObjectExperienceRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var bt40Renderer: TextRenderer? = null
    private var bt45cRenderer: TextRenderer? = null

    private var surfaceWidth = 0
    private var surfaceHeight = 0
    private lateinit var backgroundRenderHelper: BackgroundRenderHelper


    override fun onSurfaceCreated(gl: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)


        bt40Renderer = TextRenderer().apply {
            setText(context, "BT-40 Detected", 512, 128)
        }
        bt45cRenderer = TextRenderer().apply {
            setText(context, "BT-45C Detected", 512, 128)
        }

        backgroundRenderHelper = BackgroundRenderHelper()
        CameraDevice.getInstance().setClippingPlane(0.03f, 70.0f)

    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height

        MaxstAR.onSurfaceChanged(width, height)
    }


    override fun onDrawFrame(gl: GL10?) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight)

        val state = TrackerManager.getInstance().updateTrackingState()
        val trackingResult = state.trackingResult
        val image: TrackedImage = state.image
        val projectionMatrix = CameraDevice.getInstance().projectionMatrix
        val backgroundPlaneInfo = CameraDevice.getInstance().backgroundPlaneInfo

        backgroundRenderHelper.drawBackground(image, projectionMatrix, backgroundPlaneInfo)

        var bt40Detected = false
        var bt45cDetected = false

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        //Yes double check usage
        for (i in 0 until trackingResult.count) {
            val trackable = trackingResult.getTrackable(i)

            //Log.i(TAG, "Image width : " + trackable.getWidth() + ", height : " + trackable.getHeight());

            when (trackable.name) {
                "bt40" -> {
                    bt40Detected = true
                    bt40Renderer?.apply {
                        setProjectionMatrix(projectionMatrix)
                        setTransform(trackable.poseMatrix)
                        setScale(trackable.width, trackable.height, 0.1f)
                        draw()
                    }
                }

                "bt45c" -> {
                    bt45cDetected = true
                    bt45cRenderer?.apply {
                        setProjectionMatrix(projectionMatrix)
                        setTransform(trackable.poseMatrix)
                        setScale(trackable.width, trackable.height, 0.1f)
                        draw()
                    }
                }

//                else -> {
//                    coloredCubeRenderer.setProjectionMatrix(projectionMatrix)
//                    coloredCubeRenderer.setTransform(trackable.poseMatrix)
//                    coloredCubeRenderer.setTranslate(0, 0, -0.1f)
//                    coloredCubeRenderer.setScale(trackable.width, trackable.height, -0.1f)
//                    coloredCubeRenderer.draw()
//                }
            }
        }


    }


}