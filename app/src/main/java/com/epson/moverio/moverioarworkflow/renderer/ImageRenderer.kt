package com.epson.moverio.moverioarworkflow.renderer

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.epson.moverio.moverioarworkflow.util.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class ImageRenderer : BaseRenderer() {

    private val indexBuffer: ShortBuffer = ByteBuffer.allocateDirect(6 * 2)
        .order(ByteOrder.nativeOrder()).asShortBuffer().apply {
            put(shortArrayOf(0, 1, 2, 0, 2, 3))
            position(0)
        }

    private val textureCoordBuffer: FloatBuffer = ByteBuffer.allocateDirect(8 * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(
                floatArrayOf(
                    0f, 1f,  // top-left
                    0f, 0f,  // bottom-left
                    1f, 0f,  // bottom-right
                    1f, 1f   // top-right
                )
            )
            position(0)
        }

    private var textureId = 0

    init {
        val vertexBufferData = floatArrayOf(
            -0.5f, 0.5f, 0f, // top-left
            -0.5f, -0.5f, 0f, // bottom-left
            0.5f, -0.5f, 0f, // bottom-right
            0.5f, 0.5f, 0f  // top-right
        )

        vertexBuffer = ByteBuffer.allocateDirect(vertexBufferData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(vertexBufferData)
                position(0)
            }

        textureCoordBuff = textureCoordBuffer
        indexBuffer = this.indexBuffer

        val vertexShaderCode = """
            attribute vec4 a_position;
            attribute vec2 a_texCoord;
            uniform mat4 u_mvpMatrix;
            varying vec2 v_texCoord;
            void main() {
                gl_Position = u_mvpMatrix * a_position;
                v_texCoord = a_texCoord;
            }
        """

        val fragmentShaderCode = """
            precision mediump float;
            varying vec2 v_texCoord;
            uniform sampler2D u_texture;
            void main() {
                gl_FragColor = texture2D(u_texture, v_texCoord);
            }
        """

        shaderProgramId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode)

        positionHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_position")
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_texCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_mvpMatrix")
        textureHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_texture")
    }

    fun loadBitmap(bitmap: Bitmap) {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        textureId = textureIds[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    override fun draw() {
        GLES20.glUseProgram(shaderProgramId)

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(
            textureCoordHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureCoordBuff
        )
        GLES20.glEnableVertexAttribArray(textureCoordHandle)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, translation, 0, rotation, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scale, 0)
        Matrix.multiplyMM(modelMatrix, 0, transform, 0, modelMatrix, 0)
        Matrix.multiplyMM(localMvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, localMvpMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisable(GLES20.GL_BLEND)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }
}