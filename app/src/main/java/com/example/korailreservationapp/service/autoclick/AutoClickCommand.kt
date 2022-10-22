package com.example.korailreservationapp.service.autoclick

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.N)
class AutoClickCommand(private val service: AccessibilityService) {

    fun click(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 1))
        service.dispatchGesture(gestureBuilder.build(), null, null)
    }

    fun swipe(fromX: Float, fromY: Float, toX: Float, toY: Float, duration: Long) {
        val path = Path()
        path.moveTo(fromX, fromY)
        path.lineTo(toX, toY)
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, duration))
        service.dispatchGesture(gestureBuilder.build(), null, null)
    }

    suspend fun moveShortScrollUp() {
        swipe(1000F, 1900F, 1000F, 2000F, 100)
    }

    suspend fun moveLongScrollUp() {
        swipe(1000F, 1850F, 1000F, 2000F, 1)
    }

    suspend fun moveShortcutScrollDown() = withContext(Dispatchers.Main) {
        swipe(1000F, 2000F, 1000F, 1900F, 100)
    }

    suspend fun moveLongScrollDown() {
        swipe(1000F, 2000F, 1000F, 500F, 1)
    }
}