/*
 * Copyright 2016 Maxst, Inc. All Rights Reserved.
 */

package com.epson.moverio.moverioarworkflow.renderer;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.epson.moverio.moverioarworkflow.util.ShaderUtil;
import com.maxst.videoplayer.VideoPlayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VideoRenderer extends BaseRenderer {

    private static final String VERTEX_SHADER_SRC =
            "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texCoord;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform mat4 u_mvpMatrix;\n" +
                    "void main()							\n" +
                    "{										\n" +
                    "	gl_Position = u_mvpMatrix * a_position;\n" +
                    "	v_texCoord = a_texCoord; 			\n" +
                    "}										\n";

//    private static final String FRAGMENT_SHADER_SRC =
//            "precision mediump float;\n" +
//                    "varying vec2 v_texCoord;\n" +
//                    "uniform sampler2D u_texture;\n" +
//                    "void main(void)\n" +
//                    "{\n" +
//                    "	gl_FragColor = texture2D(u_texture, v_texCoord);\n" +
//                    "}\n";

    private static final String FRAGMENT_SHADER_SRC =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 v_texCoord;\n" +
                    "uniform samplerExternalOES u_texture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(u_texture, v_texCoord);\n" +
                    "}\n";

    private static final float[] VERTEX_BUF = {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
    };

    private static final short[] INDEX_BUF = {
            0, 1, 2, 2, 3, 0
    };

    private static final float[] TEXTURE_COORD_BUF = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private VideoPlayer videoPlayer;
    private boolean videoSizeAcquired = false;

    public VideoRenderer() {
        super();
        ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX_BUF.length * Float.SIZE / 8);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(VERTEX_BUF);
        vertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(INDEX_BUF.length * Integer.SIZE / 8);
        bb.order(ByteOrder.nativeOrder());
        indexBuffer = bb.asShortBuffer();
        indexBuffer.put(INDEX_BUF);
        indexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(TEXTURE_COORD_BUF.length * Float.SIZE / 8);
        bb.order(ByteOrder.nativeOrder());
        textureCoordBuff = bb.asFloatBuffer();
        textureCoordBuff.put(TEXTURE_COORD_BUF);
        textureCoordBuff.position(0);

        shaderProgramId = ShaderUtil.createProgram(VERTEX_SHADER_SRC, FRAGMENT_SHADER_SRC);

        positionHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_position");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramId, "a_texCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_mvpMatrix");
        textureHandle = GLES20.glGetUniformLocation(shaderProgramId, "u_texture");

        textureNames = new int[1];
    }

    public void draw() {

        if (videoPlayer == null) {
            Log.e(" Giles VideoRenderer", "VideoPlayer is null");
            return;
        }

        if (!videoSizeAcquired) {
            int videoWidth = videoPlayer.getVideoWidth();
            int videoHeight = videoPlayer.getVideoHeight();

            Log.d("Giles VideoRenderer", "Video size check: " + videoWidth + "x" + videoHeight);

            if (videoWidth > 0 && videoHeight > 0) {
                Log.d("Giles VideoRenderer", "Video size acquired!");
                videoSizeAcquired = true;

                GLES20.glGenTextures(1, textureNames, 0);
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureNames[0]);
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexImage2D(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0, GLES20.GL_RGB,
                        videoWidth, videoHeight, 0,
                        GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);

                videoPlayer.setTexture(textureNames[0]);
            } else {
                return; // Keep skipping this frame
            }
        }

        if (videoPlayer.getState() != VideoPlayer.STATE_PLAYING) {
            return;
        }

        videoPlayer.update();
        if (!videoPlayer.isTextureDrawable()) {
            Log.d("Giles VideoRenderer", "Texture not drawable yet");
            return;
        }


        GLES20.glUseProgram(shaderProgramId);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, textureCoordBuff);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, translation, 0, rotation, 0);
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, scale, 0);
        Matrix.multiplyMM(modelMatrix, 0, transform, 0, modelMatrix, 0);


        Matrix.multiplyMM(localMvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, localMvpMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(textureHandle, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureNames[0]);

        Log.d("Giles VideoRenderer", "Drawing with texture ID: " + textureNames[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDEX_BUF.length,
                GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    public void setVideoPlayer(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;
    }
}
