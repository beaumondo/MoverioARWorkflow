package com.epson.moverio.moverioarworkflow.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import androidx.core.content.ContextCompat
import com.epson.moverio.moverioarworkflow.R
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TextRenderer : BaseRenderer() {

    private val INDEX_BUF = shortArrayOf(
        0, 1, 2,
        0, 2, 3
    )


    private val TEXTURE_COORD_BUF = floatArrayOf(
        0.0f, 1.0f,  // Top-left
        0.0f, 0.0f,  // Bottom-left
        1.0f, 0.0f,  // Bottom-right
        1.0f, 1.0f   // Top-right
    )

    private val indexBuffer = ByteBuffer
        .allocateDirect(INDEX_BUF.size * 2) // Each short is 2 bytes
        .order(ByteOrder.nativeOrder())
        .asShortBuffer()
        .apply {
            put(INDEX_BUF)
            position(0)
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 48f
    }

    private var textureId = 0

    init {

        val VERTEX_BUF = floatArrayOf(
            -0.5f, 0.5f, 0.0f, // Top-left
            -0.5f, -0.5f, 0.0f, // Bottom-left
            0.5f, -0.5f, 0.0f, // Bottom-right
            0.5f, 0.5f, 0.0f // Top-right
        )

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_BUF.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(VERTEX_BUF)
                position(0)
            }

        textureCoordBuff =
            ByteBuffer.allocateDirect(TEXTURE_COORD_BUF.size * 4) // Each float is 4 bytes
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(TEXTURE_COORD_BUF)
                    position(0)
                }

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

        shaderProgramId = createShaderProgram(vertexShaderCode, fragmentShaderCode)

        positionHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_position")
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_texCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_mvpMatrix")
        textureHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_texture")
    }

    private fun createShaderProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        val program = GLES20.glCreateProgram()
        if (program == 0) {
            throw RuntimeException("Error creating OpenGL program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(program)
            throw RuntimeException(
                "Error linking OpenGL program: ${
                    GLES20.glGetProgramInfoLog(
                        program
                    )
                }"
            )
        }

        return program
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) {
            throw RuntimeException("Error creating shader")
        }

        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val errorLog = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Error compiling shader:\n$errorLog\nShader code:\n$shaderCode")
        }

        return shader
    }

    fun setText(context: Context, text: String, width: Int, height: Int) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        //TODO
        val backgroundColourARText = ContextCompat.getColor(context, R.color.epson)
        //
        canvas.drawColor(backgroundColourARText)
        canvas.drawText(text, 20f, height / 2f, paint)

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        if (!bitmap.isRecycled) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        }
    }

    override fun draw() {
        GLES20.glUseProgram(shaderProgramId)

        if (vertexBuffer != null) {
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glEnableVertexAttribArray(positionHandle)
        } else {
            throw IllegalStateException("Giles: vertexBuffer is null")
        }


        if (textureCoordBuff == null) {
            throw IllegalStateException("textureCoordBuff is null")
        }
        GLES20.glEnableVertexAttribArray(textureCoordHandle)
        GLES20.glVertexAttribPointer(
            textureCoordHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureCoordBuff
        )

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, translation, 0, rotation, 0)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scale, 0)
        Matrix.multiplyMM(modelMatrix, 0, transform, 0, modelMatrix, 0)
        Matrix.multiplyMM(localMvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, localMvpMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
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