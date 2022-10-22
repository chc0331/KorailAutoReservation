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
import com.example.korailreservationapp.utils.getPosition
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

const val SERVICE_RUNNING_MAX_TIME = 10 * 60000

@RequiresApi(Build.VERSION_CODES.N)
class ReservationController(private val service: KorailReservationService) {

    private val TAG = this.javaClass.simpleName
    private var collector = NodeInfoCollector(service)
    private var command = AutoClickCommand(service)
    private var prevDay: AccessibilityNodeInfo? = null
    private var nextDay: AccessibilityNodeInfo? = null
    private var job: Job? = null
    private val _serviceState = MutableSharedFlow<Boolean>()
    var serviceState: SharedFlow<Boolean> = _serviceState

    fun startAutoReservation(checkedTicket: List<Pair<Ticket, Int>>) =
        CoroutineScope(Dispatchers.Default).launch {
            //1. get next, prev nodeInfo
            _serviceState.emit(true)
            delay(200)
            command.moveLongScrollUp()
            delay(500)
            collectPrefNextNode()

            var findNode: AccessibilityNodeInfo?
            var startTime = System.currentTimeMillis()

            job = launch {
                while (true) {
                    if ((System.currentTimeMillis() - startTime) > SERVICE_RUNNING_MAX_TIME) {
                        _serviceState.emit(false)
                        break
                    }

                    //update new screen
                    command.moveLongScrollUp()
                    delay(500)
                    nextDay?.let {
                        command.click(it.getPosition().first, it.getPosition().second)
                    }
                    delay(1000)
                    prevDay?.let {
                        command.click(it.getPosition().first, it.getPosition().second)
                    }
                    delay(1000)

                    //find correct screen
                    findScreen(checkedTicket)

                    //check if ticket can reserve
                    findNode = checkIfTicketsReservable(checkedTicket)
                    if (findNode != null) {
                        Log.d(TAG, "findNode!!!")
                        _serviceState.emit(false)
                        break
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
                    prevDay = node
                }
                if (node.text.equals(AFTER_DAY)) {
                    nextDay = node
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
                            if (it.isEmpty()) {
                                //todo : need to implement service finish when screen is not on ticket reservation list screen.
//                                service.stoppedByTouch()
//                                cancel()
                            }
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