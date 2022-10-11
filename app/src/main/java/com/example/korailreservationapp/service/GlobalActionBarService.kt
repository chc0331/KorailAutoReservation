package com.example.korailreservationapp.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.korailreservationapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GlobalActionBarService : AccessibilityService() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onServiceConnected() {
        super.onServiceConnected()
        setTheme(R.style.Theme_KorailReservationApp)

        serviceInfo = serviceInfo.apply {
            eventTypes =
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                        AccessibilityEvent.TYPE_WINDOWS_CHANGED
            flags = flags or AccessibilityServiceInfo.DEFAULT
        }
        createView()

/*        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
//        val layoutParams = WindowManager.LayoutParams()
//        layoutParams.apply {
//            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
//            format = PixelFormat.TRANSLUCENT
//            flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//            width = WindowManager.LayoutParams.WRAP_CONTENT
//            height = WindowManager.LayoutParams.WRAP_CONTENT
//            gravity = Gravity.TOP
//        }
//        val inflater = LayoutInflater.from(this)
//        inflater.inflate(R.layout.action_bar, layout)
//        wm.addView(layout, layoutParams)
//
//        configurePowerButton()
//        configureVolumeButton()
//        configureScrollButton()
//        configureSwipeButton()
*/
    }

    private fun createView() {
        val layout = ConstraintLayout(this)
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.BOTTOM
        }
        val view = LayoutInflater.from(this)
            .inflate(R.layout.reservation_service_layout, layout)



        wm.addView(view, layoutParams)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val nodeInfo = event?.source
        getChild(nodeInfo, 0)
    }

    private fun getChild(nodeInfo: AccessibilityNodeInfo?, level: Int) {
        nodeInfo?.let {
            for (i in 0 until it.childCount) {
                getChild(it.getChild(i), level + 1)
            }
        }
    }

    override fun onInterrupt() {
        Log.d("heec.choi", "onInterrupt")
    }

//    private fun configurePowerButton() {
//        val powerButton = layout.findViewById<Button>(R.id.power)
//        powerButton.setOnClickListener {
//            performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
//        }
//    }
//
//    private fun configureVolumeButton() {
//        val volumeUpButton = layout.findViewById<Button>(R.id.volume_up)
//        volumeUpButton.setOnClickListener {
//            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
//            audioManager.adjustStreamVolume(
//                AudioManager.STREAM_MUSIC,
//                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI
//            )
//        }
//    }
//
//    private fun configureScrollButton() {
//        val scrollButton = layout.findViewById<Button>(R.id.scroll)
//        scrollButton.setOnClickListener {
//            val scrollable = findScrollableNode(rootInActiveWindow)
//            scrollable?.let {
//                it.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD.id)
//            }
//        }
//    }
//
//    private fun findScrollableNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
//        val deque = ArrayDeque<AccessibilityNodeInfo>()
//        deque.add(root)
//        while (!deque.isEmpty()) {
//            val node = deque.removeFirst()
//            if (node.actionList.contains(
//                    AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD
//                )
//            ) return node
//
//            for (i in 0 until node.childCount) {
//                deque.addLast(node.getChild(i))
//            }
//        }
//        return null
//    }
//
//    @RequiresApi(Build.VERSION_CODES.N)
//    private fun configureSwipeButton() {
//        val button = layout.findViewById<Button>(R.id.swipe)
//        button.setOnClickListener {
//            val swipePath = Path()
//            swipePath.moveTo(1000f, 1000f)
//            swipePath.lineTo(100f, 100f)
//            val gestureBuilder = GestureDescription.Builder()
//            gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, 500))
//            dispatchGesture(gestureBuilder.build(), null, null)
//        }
//    }


}