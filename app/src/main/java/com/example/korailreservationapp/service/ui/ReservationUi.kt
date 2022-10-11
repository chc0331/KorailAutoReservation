package com.example.korailreservationapp.service.ui

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.media.Image
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.korailreservationapp.R
import com.example.korailreservationapp.databinding.ReservationServiceLayoutBinding
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.utils.hide
import com.example.korailreservationapp.utils.show
import com.example.korailreservationapp.utils.toggleVisibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class ReservationUi(private val service: KorailReservationService) {

    private var binding: ReservationServiceLayoutBinding
    private var wm = service.getSystemService(AccessibilityService.WINDOW_SERVICE) as WindowManager
    private var layoutParams = WindowManager.LayoutParams()
    private val adapter = TicketListAdapter()
    private var expanded = false
    private var collectRunning = false

    init {
        layoutParams.apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags =
                flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = convertLayoutParamsDp(50F)
            gravity = Gravity.TOP
        }
        binding = ReservationServiceLayoutBinding.inflate(
            LayoutInflater.from(service),
            null, false
        ).apply {
            wm.addView(root, layoutParams)
        }
        initComponents()
    }

    private fun initComponents() {
        binding.run {
            commandButton.setOnClickListener {
                start.toggleVisibility()
                exit.toggleVisibility()
                reservation.toggleVisibility()
            }

            exit.setOnClickListener { service.disableSelf() }

            hide.setOnClickListener {
                if (expanded) {
                    start.hide()
                    exit.hide()
                    reservation.hide()
                    commandButton.hide()
                    ticketList.hide()
                    layoutParams.height = convertLayoutParamsDp(50F)
                    wm.updateViewLayout(binding.root, layoutParams)
                    expanded = false
                    (it as ImageView).setImageResource(R.drawable.downward)
                } else {
                    ticketList.show()
                    commandButton.show()
                    layoutParams.height = convertLayoutParamsDp(400F)
                    wm.updateViewLayout(binding.root, layoutParams)
                    expanded = true
                    (it as ImageView).setImageResource(R.drawable.upward)
                }
            }

            val list = ArrayList<Ticket>()
            adapter.submitList(list)
            ticketList.adapter = adapter
            start.setOnClickListener {
                if (collectRunning) {
                    collectRunning = false
                    (it as ImageButton).setImageResource(R.drawable.play)
                } else {
                    collectRunning = true
                    (it as ImageButton).setImageResource(R.drawable.stop)
                    collectData()
                }
            }
        }
    }

    fun collectData() {
        CoroutineScope(Dispatchers.Main).launch {
            ReservationController.collectData(service).collectLatest {
                adapter.submitList(it)
            }
        }
    }

    private fun convertLayoutParamsDp(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value, service.resources.displayMetrics
    ).toInt()
}


