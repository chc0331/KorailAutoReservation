package com.example.korailreservationapp.service.data

import android.view.accessibility.AccessibilityNodeInfo

data class Ticket(
    val id: Int,
    val train: CharSequence,
    val startInfo: CharSequence,
    val destinationInfo: CharSequence,
    val seat: AccessibilityNodeInfo,
    val specialSeat: AccessibilityNodeInfo
)