package com.example.korailreservationapp.service.autoclick

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
object AutoClickCommand {

    fun click(x: Float, y: Float, service: AccessibilityService) {
        Log.d("heec.choi", "click : x y " + x + y)
        val path = Path()
        path.moveTo(x, y)
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 500))
        service.dispatchGesture(gestureBuilder.build(), null, null)
    }

    fun swipe(fromX: Float, fromY: Float, toX: Float, toY: Float, service: AccessibilityService) {
        val path = Path()
        path.moveTo(fromX, fromY)
        path.lineTo(toX, toY)
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 500))
        service.dispatchGesture(gestureBuilder.build(), null, null)
    }
}