package com.epson.moverio.moverioarworkflow.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.enableImmersiveMode() {
    val decorView = window.decorView
    decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

@Suppress("DEPRECATION")
fun AppCompatActivity.enterFullscreen() {
    val decorView = window.decorView
    decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
    actionBar?.hide()
}

fun loadBitmapFramesFromAssets(context: Context, folderName: String): List<Bitmap> {
    val bitmaps = mutableListOf<Bitmap>()
    val assetManager = context.assets
    val fileList = assetManager.list(folderName)?.sorted() ?: return emptyList()

    for (filename in fileList) {
        val inputStream = assetManager.open("$folderName/$filename")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmaps.add(bitmap)
        inputStream.close()
    }

    return bitmaps
}