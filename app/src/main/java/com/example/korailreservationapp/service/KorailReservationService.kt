package com.example.korailreservationapp.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.korailreservationapp.R
import com.example.korailreservationapp.service.ui.ReservationUi

class KorailReservationService : AccessibilityService() {

    private lateinit var serviceUi: ReservationUi
    var recentNodeInfo: AccessibilityNodeInfo? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onServiceConnected() {
        super.onServiceConnected()
        setTheme(R.style.Theme_KorailReservationApp)

        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            flags = flags or AccessibilityServiceInfo.DEFAULT
        }
        serviceUi = ReservationUi(this)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        recentNodeInfo = event?.source
    }

    override fun onInterrupt() {
        Log.d("heec.choi", "onInterrupt")
    }

    fun getAllNodeInfos(): List<AccessibilityNodeInfo> {
        val list = ArrayList<AccessibilityNodeInfo>()
        recentNodeInfo?.let {
            getChild(it, list)
        }
        return list
    }

    private fun getChild(
        nodeInfo: AccessibilityNodeInfo?,
        list: ArrayList<AccessibilityNodeInfo>
    ) {
        nodeInfo?.let {
            list.add(nodeInfo)
            for (i in 0 until it.childCount) {
                getChild(it.getChild(i), list)
            }
        }
    }


}