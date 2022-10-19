package com.example.korailreservationapp.service.ui

import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.korailreservationapp.service.KorailReservationService
import com.example.korailreservationapp.service.autoclick.AutoClickCommand
import com.example.korailreservationapp.service.data.AFTER_DAY
import com.example.korailreservationapp.service.data.BEFORE_DAY
import com.example.korailreservationapp.service.data.SOLD_OUT
import com.example.korailreservationapp.service.data.Ticket
import com.example.korailreservationapp.utils.containsTicket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.N)
class ReservationController(private val service: KorailReservationService) {

    private var collector = NodeInfoCollector(service)
    private var command = AutoClickCommand(service)
    private var beforeNode: AccessibilityNodeInfo? = null
    private var afterNode: AccessibilityNodeInfo? = null
    private var job: Job? = null
    private val _serviceState = MutableStateFlow(false)
    var serviceState: StateFlow<Boolean> = _serviceState

    private val _serviceFinish = MutableStateFlow(false)
    var serviceFinish: StateFlow<Boolean> = _serviceFinish

    fun startAutoReservation(checkedTicket: List<Pair<Ticket, Int>>) =
        CoroutineScope(Dispatchers.Main).launch {
            //1. get next, prev nodeInfo
            _serviceState.emit(true)
            delay(200)
            command.moveLongScrollUp()
            delay(500)
            collectPrefNextNode()

            var findNode: AccessibilityNodeInfo? = null
            job = launch {
                while (true) {
                    //2. find correct screen
                    command.moveLongScrollUp()
                    findScreen(checkedTicket)

                    //3. check if ticket can reserve
                    findNode = checkIfTicketsReservable(checkedTicket)
                    if (findNode != null) {
                        _serviceState.emit(false)
                        cancel()
                    }
                }
            }
        }

    fun stopAutoReservation() = CoroutineScope(Dispatchers.Default).launch {
        job?.let {
            if (it.isActive) {
                _serviceState.emit(false)
                it.cancel()
            }
        }
    }

    private suspend fun collectPrefNextNode() = withContext(Dispatchers.Default) {
        collector.collectPrevNextNode().collectLatest {
            for (node: AccessibilityNodeInfo in it) {
                if (node.text.equals(BEFORE_DAY)) {
                    beforeNode = node
                }
                if (node.text.equals(AFTER_DAY)) {
                    afterNode = node
                }
            }
        }
    }

    private suspend fun findScreen(checkedTicket: List<Pair<Ticket, Int>>) =
        withContext(Dispatchers.Main) {
            try {
                withTimeout(12000) {
                    while (true) {
                        var contain = true
                        collector.collectTickets().collect {
                            for (item in checkedTicket) {
                                val ticket = item.first
                                if (!it.containsTicket(ticket)) {
                                    contain = false
                                    break
                                }
                            }
                        }

                        if (contain) break
                        else {
                            command.moveShortcutScrollDown()
                            delay(500)
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.d("heec.choi", "Time out!!")
            }
        }

    private suspend fun checkIfTicketsReservable(checkedTicket: List<Pair<Ticket, Int>>): AccessibilityNodeInfo? =
        withContext(Dispatchers.Default) {
            var nodeInfo: AccessibilityNodeInfo? = null
            collector.collectTickets().collect { tickets ->
                for (ticket: Ticket in tickets) {
                    var state = checkedTicket.containsTicket(ticket)
                    if (state and (1 shl 0) > 0) {
                        if (!ticket.seat.text.equals(SOLD_OUT)) {
                            nodeInfo = ticket.seat
                            break
                        }
                    }
                    if (state and (1 shl 1) > 0) {
                        if (!ticket.specialSeat.text.equals(SOLD_OUT)) {
                            nodeInfo = ticket.specialSeat
                            break
                        }
                    }
                }
            }
            return@withContext nodeInfo
        }

}