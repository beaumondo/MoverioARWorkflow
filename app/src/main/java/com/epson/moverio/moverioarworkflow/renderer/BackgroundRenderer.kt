package com.epson.moverio.moverioarworkflow.renderer

import com.maxst.ar.TrackedImage

abstract class BackgroundRenderer : BaseRenderer() {
    abstract fun draw(trackedImage: TrackedImage)
}