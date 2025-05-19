package com.epson.moverio.moverioarworkflow.renderer

import com.maxst.ar.ColorFormat
import com.maxst.ar.Matrix
import com.maxst.ar.TrackedImage

class BackgroundRenderHelper {

    private var backgroundRenderer: BackgroundRenderer? = null

    fun drawBackground(
        trackedImage: TrackedImage,
        projectionMatrix: FloatArray,
        backgroundPlaneInfo: FloatArray,
        flipHorizontal: Boolean = false,
        flipVertical: Boolean = false
    ) {
        if (backgroundRenderer == null) {
            backgroundRenderer = when (trackedImage.format) {
                ColorFormat.YUV420sp -> Yuv420spRenderer()
                ColorFormat.YUV420_888 -> Yuv420_888Renderer()
                else -> return
            }
        }

        backgroundRenderer?.apply {
            setProjectionMatrix(projectionMatrix)
            setTransform(
                calculateBackgroundPlaneMatrix(backgroundPlaneInfo, flipHorizontal, flipVertical)
            )
            draw(trackedImage)
        }
    }

    private fun calculateBackgroundPlaneMatrix(
        backgroundPlaneInfo: FloatArray,
        flipHorizontal: Boolean,
        flipVertical: Boolean
    ): FloatArray {
        val matrix = FloatArray(16)
        Matrix.setIdentityM(matrix, 0)

        if (backgroundPlaneInfo[0] >= 0) {
            val maxX = backgroundPlaneInfo[0]
            val maxY = backgroundPlaneInfo[1]
            val minX = backgroundPlaneInfo[2]
            val minY = backgroundPlaneInfo[3]
            val z = backgroundPlaneInfo[4]

            var x = maxX - minX
            var y = maxY - minY

            if (flipVertical) x = -x
            if (flipHorizontal) y = -y

            val position = floatArrayOf(
                (maxX + minX) / 2.0f,
                (maxY + minY) / 2.0f,
                z
            )

            Matrix.translateM(matrix, 0, position[0], position[1], position[2])
            Matrix.scaleM(matrix, 0, x, y, 1.0f)
        }

        return matrix
    }
}
