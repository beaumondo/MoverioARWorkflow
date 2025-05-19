package com.epson.moverio.moverioarworkflow.renderer

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.epson.moverio.moverioarworkflow.util.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BitmapSequenceRenderer : BaseRenderer() {
    private val frameTextures = mutableListOf<Int>()
    private var currentFrameIndex = 0
    private var lastFrameTime = 0L
    private val frameDurationMillis = 66L // ~15 FPS
    private var folderName: String? = null

    private val VERTEX_BUF = floatArrayOf(
        -0.5f, 0.5f, 0.0f,   // Top-left
        -0.5f, -0.5f, 0.0f,  // Bottom-left
        0.5f, -0.5f, 0.0f,   // Bottom-right
        0.5f, 0.5f, 0.0f     // Top-right
    )

    private val TEXTURE_COORD_BUF = floatArrayOf(
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )

    private val INDEX_BUF = shortArrayOf(0, 1, 2, 0, 2, 3)

    init {
        setupBuffers()
        setupShader()
    }

    private var initialized = false
    fun isNotInitialized(): Boolean = !initialized

    private fun setupBuffers() {
        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_BUF.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(VERTEX_BUF)
                position(0)
            }

        textureCoordBuff = ByteBuffer.allocateDirect(TEXTURE_COORD_BUF.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(TEXTURE_COORD_BUF)
                position(0)
            }

        indexBuffer = ByteBuffer.allocateDirect(INDEX_BUF.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(INDEX_BUF)
                position(0)
            }
    }

    private fun setupShader() {
        val vertexShaderCode = """
            attribute vec4 a_position;
            attribute vec2 a_texCoord;
            uniform mat4 u_mvpMatrix;
            varying vec2 v_texCoord;
            void main() {
                gl_Position = u_mvpMatrix * a_position;
                v_texCoord = a_texCoord;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            varying vec2 v_texCoord;
            uniform sampler2D u_texture;
            void main() {
                gl_FragColor = texture2D(u_texture, v_texCoord);
            }
        """.trimIndent()

        shaderProgramId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode)

        positionHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_position")
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_texCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_mvpMatrix")
        textureHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_texture")
    }

    fun loadBitmaps(folder: String, bitmapList: List<Bitmap>) {

        if (initialized && folder == folderName) return
        frameTextures.clear()

        val textureIds = IntArray(bitmapList.size)
        GLES20.glGenTextures(bitmapList.size, textureIds, 0)

        for (i in bitmapList.indices) {
            val bitmap = bitmapList[i]
            val textureId = textureIds[i]

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            //bitmap.recycle()

            frameTextures.add(textureId)
        }

        folderName = folder
        initialized = true
    }

    override fun draw() {
        if (frameTextures.isEmpty()) return

        val now = System.currentTimeMillis()
        if (now - lastFrameTime > frameDurationMillis) {
            currentFrameIndex = (currentFrameIndex + 1) % frameTextures.size
            lastFrameTime = now
        }

        GLES20.glUseProgram(shaderProgramId)

        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer as java.nio.Buffer
        )
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glVertexAttribPointer(
            textureCoordHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureCoordBuff as java.nio.Buffer
        )
        GLES20.glEnableVertexAttribArray(textureCoordHandle)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, translation, 0, rotation, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scale, 0)
        Matrix.multiplyMM(modelMatrix, 0, transform, 0, modelMatrix, 0)
        Matrix.multiplyMM(localMvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, localMvpMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTextures[currentFrameIndex])
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            INDEX_BUF.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

}