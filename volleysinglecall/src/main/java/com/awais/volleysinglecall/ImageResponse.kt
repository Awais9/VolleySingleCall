package com.awais.volleysinglecall

import android.graphics.Bitmap

interface ImageResponse {
    fun onSuccess(response: Bitmap)
    fun onFailure(message: String)
}