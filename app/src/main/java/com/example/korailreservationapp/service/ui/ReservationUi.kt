package com.example.korailreservationapp.service.ui

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.korailreservationapp.R
import com.example.korailreservationapp.databinding.ReservationServiceLayoutBinding
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.autoclick.AutoClickCommand
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.utils.hide
import com.example.korailreservationapp.utils.show
import com.example.korailreservationapp.utils.toggleVisibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class ReservationUi(private val service: KorailReservationService) {

    private var collector: NodeInfoCollector
    private var checkedState = ArrayList<Int>()
    private var binding: ReservationServiceLayoutBinding
    private var wm = service.getSystemService(AccessibilityService.WINDOW_SERVICE) as WindowManager
    private var layoutParams = WindowManager.LayoutParams()
    private var expanded = false
    private var checkedSeatList = ArrayList<Pair<Ticket, Int>>()
    private val adapter = TicketListAdapter { idx, seatType, checked ->
        checkedState[idx] =
            if (checked && seatType == 0)
                checkedState[idx] or (1 shl 0)
            else if (!checked && seatType == 0)
                checkedState[idx] xor (1 shl 0)
            else if (checked && seatType == 1)
                checkedState[idx] or (1 shl 1)
            else
                checkedState[idx] xor (1 shl 1)
    }
    private var command: AutoClickCommand
    private var controller: ReservationController
    private var serviceRunning = false

    init {
        layoutParams.apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSLUCENT
            flags =
                flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = convertLayoutParamsDp(70F)
            gravity = Gravity.TOP
        }
        binding = ReservationServiceLayoutBinding.inflate(
            LayoutInflater.from(service),
            null, false
        ).apply {
            wm.addView(root, layoutParams)
        }
        collector = NodeInfoCollector(service)
        command = AutoClickCommand(service)
        controller = ReservationController(service)
        initComponents()
        observeController()
    }

    private fun initComponents() {
        binding.run {
            commandButton.setOnClickListener {
                refresh.toggleVisibility()
                exit.toggleVisibility()
                reservation.toggleVisibility()
            }

            exit.setOnClickListener { service.disableSelf() }

            hide.setOnClickListener {
                if (expanded) {
                    collapse()
                } else {
                    expand()
                }
            }

            refresh.setOnClickListener {
                collectTickets()
            }

            reservation.setOnClickListener {
                if (serviceRunning) {
                    controller.stopAutoReservation()
                    serviceRunning = false
                } else {
                    serviceRunning = true
                    checkedSeatList.clear()
                    for (idx in 0 until checkedState.size) {
                        if (checkedState[idx] > 0) {
                            val ticket = adapter.currentList[idx]
                            checkedSeatList.add(Pair(ticket, checkedState[idx]))
                        }
                    }
                    if (checkedSeatList.isEmpty())
                        Toast.makeText(service, "선택된 티켓이 없습니다.", Toast.LENGTH_SHORT).show()
                    else {
                        collapse()
                        controller.startAutoReservation(checkedSeatList)
                    }
                }
            }
            ticketList.adapter = adapter
        }
    }

    private fun observeController() = CoroutineScope(Dispatchers.Main).launch {
        controller.serviceState.collect {
            if (it) {
                Toast.makeText(service, "Auto reservation start", Toast.LENGTH_SHORT).show()
                binding.reservation.setImageResource(R.drawable.stop)
            } else {
                Toast.makeText(service, "Auto reservation stop", Toast.LENGTH_SHORT).show()
                binding.reservation.setImageResource(R.drawable.play)
            }
        }
    }

    private fun collapse() {
        binding.run {
            refresh.hide()
            exit.hide()
            reservation.hide()
            commandButton.hide()
            ticketList.hide()
            layoutParams.height = convertLayoutParamsDp(70F)
            wm.updateViewLayout(binding.root, layoutParams)
            expanded = false
            hide.setImageResource(R.drawable.downward)
        }
    }

    private fun expand() {
        binding.run {
            ticketList.show()
            commandButton.show()
            layoutParams.height = convertLayoutParamsDp(500F)
            wm.updateViewLayout(binding.root, layoutParams)
            expanded = true
            hide.setImageResource(R.drawable.upward)
        }
    }

    private fun collectTickets() {
        CoroutineScope(Dispatchers.Main).launch {
            collector.collectTickets().collectLatest {
                checkedState = MutableList(it.size) { 0 } as ArrayList<Int>
                adapter.submitList(it)
            }
        }
    }

    private fun convertLayoutParamsDp(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value, service.resources.displayMetrics
    ).toInt()

    fun onDestroy() {

    }
}


