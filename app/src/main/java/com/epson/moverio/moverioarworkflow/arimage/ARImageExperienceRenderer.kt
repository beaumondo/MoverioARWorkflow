package com.epson.moverio.moverioarworkflow.arimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.epson.moverio.moverioarworkflow.renderer.BackgroundRenderHelper
import com.epson.moverio.moverioarworkflow.renderer.BitmapSequenceRenderer
import com.epson.moverio.moverioarworkflow.renderer.ImageRenderer
import com.epson.moverio.moverioarworkflow.renderer.TextRenderer
import com.epson.moverio.moverioarworkflow.renderer.VideoRenderer
import com.epson.moverio.moverioarworkflow.utils.loadBitmapFramesFromAssets
import com.maxst.ar.CameraDevice
import com.maxst.ar.MaxstAR
import com.maxst.ar.Trackable
import com.maxst.ar.TrackedImage
import com.maxst.ar.TrackerManager
import com.maxst.videoplayer.VideoPlayer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * ARImageExperienceRenderer
 *
 * Renders content for either the BT-45C or BT-40 depending
 * on the tracked image.
 *
 *  * Author: Giles Edward Beaumont
 *  * Date: 31/12/2024
 */
class ARImageExperienceRenderer(private val context: Context) : GLSurfaceView.Renderer {


    private var surfaceWidth = 0
    private var surfaceHeight = 0
    private lateinit var backgroundRenderHelper: BackgroundRenderHelper
    private var currentStep = 1

    private lateinit var bitmapRenderer: BitmapSequenceRenderer
    private val bitmapFrameCache = mutableMapOf<String, List<Bitmap>>()

    private val imageRenderers = mutableMapOf<String, ImageRenderer>()
    private var imageLoaded = false

    private var textRenderer: TextRenderer? = null
    private var currentImage: Bitmap? = null

    private val videoRenderer = VideoRenderer()
    private var videoPlayer: VideoPlayer? = null
    private var currentVideoName: String? = null

    override fun onSurfaceCreated(gl: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        textRenderer = TextRenderer()
        bitmapRenderer = BitmapSequenceRenderer()

        videoPlayer = VideoPlayer(context)
        videoPlayer?.openVideo("moverio_history.mp4")  // Your asset video
        videoRenderer.videoPlayer = videoPlayer

        backgroundRenderHelper = BackgroundRenderHelper()
        CameraDevice.getInstance().setClippingPlane(0.03f, 70.0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height

        MaxstAR.onSurfaceChanged(width, height)
    }

    fun moveToNextStep(): Int {
        return currentStep.also {
            if (currentStep < 4) {
                currentStep += 1
            } else
                currentStep = 1
        }
    }


    fun moveToPreviousStep(): Int {
        return currentStep.also {
            if (currentStep > 1) {
                currentStep -= 1
            } else
                currentStep = 4
        }
    }

    override fun onDrawFrame(gl: GL10?) {
//        Log.d("Giles Renderer", "onDrawFrame called - Step: $currentStep")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight)

        val state = TrackerManager.getInstance().updateTrackingState()
        val trackingResult = state.trackingResult
        val image: TrackedImage = state.image
        val projectionMatrix = CameraDevice.getInstance().projectionMatrix
        val backgroundPlaneInfo = CameraDevice.getInstance().backgroundPlaneInfo

        backgroundRenderHelper.drawBackground(image, projectionMatrix, backgroundPlaneInfo)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDisable(GLES20.GL_CULL_FACE)

        //Yes double check usage
        for (i in 0 until trackingResult.count) {
            val trackable = trackingResult.getTrackable(i)
//            Log.d("Giles Tracker", "Detected Marker: ${trackable.name}")

            when (trackable.name) {
                "bt40" -> {
                    when (currentStep) {

                        1 -> {
                            displayText(
                                "Designed for Commercial",
                                -0.1f,
                                -0.06f,
                                projectionMatrix,
                                trackable
                            )
                            displayText(
                                "and Personal use",
                                0.1f,
                                -0.06f,
                                projectionMatrix,
                                trackable
                            )
                            displayImage(
                                "bt40_commercial.jpg",
                                projectionMatrix,
                                trackable,
                                widthScale = 0.9f,
                                heightScale = 0.4f,
                                offsetX = -0.1f,
                                offsetY = -0.1f
                            )
                            displayImage(
                                "bt40_personal.jpg",
                                projectionMatrix,
                                trackable,
                                widthScale = 0.9f,
                                heightScale = 0.4f,
                                offsetX = 0.1f,
                                offsetY = -0.1f
                            )
                        }

                        2 -> {
                            displayText(
                                "Si-OLED Micro-Display",
                                -0.1f,
                                -0.02f,
                                projectionMatrix,
                                trackable
                            )
                            displayText(
                                "Full HD Resolution",
                                -0.0f,
                                -0.02f,
                                projectionMatrix,
                                trackable
                            )
                            displayText(
                                "120 Inch Screen",
                                0.1f,
                                -0.02f,
                                projectionMatrix,
                                trackable
                            )
                            displayBitmaps(
                                renderer = bitmapRenderer,
                                folderName = "frames/si_oled",
                                x = 0.1f,
                                y = 0.0f,
                                projectionMatrix = projectionMatrix,
                                trackable = trackable
                            )
                        }

                        3 -> {
                            displayText(
                                "Magnetic dark shade",
                                0.05f,
                                -0.064f,
                                projectionMatrix,
                                trackable
                            )
                            displayText(
                                "for immersive or",
                                0.05f,
                                -0.052f,
                                projectionMatrix,
                                trackable
                            )
                            displayText("private usage", 0.05f, -0.04f, projectionMatrix, trackable)
                            displayImage(
                                "bt40_dark_shade.png",
                                projectionMatrix,
                                trackable,
                                widthScale = 0.99f,
                                heightScale = 0.4f,
                                offsetX = -0.0f,
                                offsetY = -0.004f
                            )
                        }

                        4 -> {
                            displayText("175mm", -0.04f, -0.09f, projectionMatrix, trackable)
                            displayText("135mm", 0.04f, -0.09f, projectionMatrix, trackable)
                            displayImage(
                                "bt40_head_size.png",
                                projectionMatrix,
                                trackable,
                                widthScale = 0.9f,
                                heightScale = 0.4f,
                                offsetX = -0.0f,
                                offsetY = -0.08f
                            )
                        }
                    }
                }

                "bt45c" -> {
                    when (currentStep) {

                        1 -> {
                            displayText(
                                "8MP Centered Camera",
                                0f,
                                -0.06f,
                                projectionMatrix,
                                trackable
                            )
                            displayBitmaps(
                                renderer = bitmapRenderer,
                                folderName = "frames/camera",
                                x = 0f,
                                y = -0.03f,
                                projectionMatrix = projectionMatrix,
                                trackable = trackable
                            )
                        }

                        2 -> {
                            displayText(
                                "Si-OLED Micro-Display",
                                -0.1f,
                                0.01f,
                                projectionMatrix,
                                trackable
                            )
                            displayText(
                                "Full HD Resolution",
                                -0.0f,
                                0.01f,
                                projectionMatrix,
                                trackable
                            )
                            displayText("120 Inch Screen", 0.1f, 0.01f, projectionMatrix, trackable)
                            displayBitmaps(
                                renderer = bitmapRenderer,
                                folderName = "frames/si_oled",
                                x = 0.1f,
                                y = 0.03f,
                                projectionMatrix = projectionMatrix,
                                trackable = trackable
                            )
                        }

                        3 -> {
                            displayImage(
                                "bt45c_audio.jpg",
                                projectionMatrix,
                                trackable,
                                widthScale = 0.9f,
                                heightScale = 0.4f,
                                offsetX = -0.0f,
                                offsetY = -0.1f
                            )
                        }

                        4 -> {
                            displayText("Fully Rugged", 0.06f, 0.03f, projectionMatrix, trackable)
                            displayText("MIL-STD-810H", 0.06f, 0.05f, projectionMatrix, trackable)
                            displayText(
                                "Helmet Compatible",
                                0.06f,
                                0.07f,
                                projectionMatrix,
                                trackable
                            )
                            displayImage(
                                "bt45c_helmet.png",
                                projectionMatrix,
                                trackable,
                                widthScale = 0.9f,
                                heightScale = 0.5f,
                                offsetX = 0.0f,
                                offsetY = -0.06f
                            )
                        }
                    }
                }
            }
        }
    }

    private fun displayText(
        content: String,
        x: Float,
        y: Float,
        projectionMatrix: FloatArray,
        trackable: Trackable
    ) {

        val textWidth = 512 // Adjust based on content size
        val textHeight = 128 // Reduce to crop the background

        textRenderer?.apply {
            setText(context, content, textWidth, textHeight)
            setProjectionMatrix(projectionMatrix)
            setTransform(trackable.poseMatrix)

            // Scale proportionally based on text dimensions
            val scaleX = (textWidth.toFloat() / 512f) * trackable.width * 0.3f
            val scaleY = (textHeight.toFloat() / 128f) * trackable.height * 0.05f

            setScale(scaleX, scaleY, 0.05f) // Scale based on actual text size
            setTranslate(x, y, -0.05f) // Adjust placement
            draw()
        }
    }

    private fun displayVideo(
        x: Float,
        y: Float,
        projectionMatrix: FloatArray,
        trackable: Trackable
    ) {
        val player = videoRenderer.videoPlayer

        if (player != null &&
            (player.state == VideoPlayer.STATE_READY || player.state == VideoPlayer.STATE_PAUSE)
        ) {
            player.start()
        }

        if (player != null &&
            player.state == VideoPlayer.STATE_PLAYING &&
            player.videoWidth > 0 &&
            player.isTextureDrawable
        ) {
            videoRenderer.apply {
                setProjectionMatrix(projectionMatrix)
                setTransform(trackable.poseMatrix)

                // Experiment with this for visibility
                setScale(trackable.width, trackable.height, 0.05f)
                setTranslate(x, y, 0.1f) // positive Z to push toward camera

                draw()
            }
        }
    }

    private fun displayBitmaps(
        renderer: BitmapSequenceRenderer,
        folderName: String,
        x: Float,
        y: Float,
        projectionMatrix: FloatArray,
        trackable: Trackable
    ) {
        //if (renderer.isNotInitialized()) {
        //val frames = loadBitmapFramesFromAssets(context, folderName)

        val frames = bitmapFrameCache.getOrPut(folderName) {
            loadBitmapFramesFromAssets(context, folderName)
        }

        renderer.setProjectionMatrix(projectionMatrix)
        renderer.loadBitmaps(folderName, frames)
        //}

        val bitmapAspectRatio = 960f / 720f  // width / height = 1.33f
        val scaleY = trackable.height * 0.5f
        val scaleX = scaleY * bitmapAspectRatio

        renderer.apply {
            setTransform(trackable.poseMatrix)
            renderer.setScale(scaleX * 0.25f, scaleY * 0.25f, 0.05f)
            // setScale(trackable.width * 0.5f, trackable.height * 0.5f, 0.05f)
            setTranslate(x, y, -0.05f)
            draw()
        }
    }

    private fun displayImage(
        fileName: String,
        projectionMatrix: FloatArray,
        trackable: Trackable,
        widthScale: Float = 1.0f,
        heightScale: Float = 1.0f,
        offsetX: Float = 0.0f,
        offsetY: Float = 0.0f
    ) {
        val renderer = imageRenderers.getOrPut(fileName) {
            try {
                val inputStream = context.assets.open("images/$fileName")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                ImageRenderer().apply {
                    loadBitmap(bitmap)
                }
            } catch (e: Exception) {
                Log.e("ARImage", "Failed to load image: $fileName", e)
                return
            }
        }

        val imageWidth = trackable.width * widthScale
        val imageHeight = trackable.height * heightScale

        renderer.apply {
            setProjectionMatrix(projectionMatrix)
            setTransform(trackable.poseMatrix)
            setScale(imageWidth, imageHeight, 1f)
            setTranslate(offsetX, offsetY, 0f)
            draw()
        }
    }
}



