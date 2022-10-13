package com.example.korailreservationapp.service.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.korailreservationapp.databinding.TicketListBinding
import com.example.korailreservationapp.service.data.Ticket

class TicketListAdapter(private val checkListener: (idx: Int, seatType: Int, checked: Boolean) -> Unit) :
    ListAdapter<Ticket, TicketListAdapter.ViewHolder>(TicketDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = getItem(position)
        holder.bind(ticket, position, checkListener)
    }

    class ViewHolder(private val binding: TicketListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            ticket: Ticket,
            idx: Int,
            listener: (idx: Int, seatType: Int, checked: Boolean) -> Unit
        ) {
            binding.run {
                trainType.text = ticket.train
                startInfoContent.text = ticket.startInfo
                destinationInfoContent.text = ticket.destinationInfo
                seat.setOnCheckedChangeListener { _, checked ->
                    listener(idx, 0, checked)
                }
                specialSeat.setOnCheckedChangeListener { _, checked ->
                    listener(idx, 1, checked)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TicketListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }


    object TicketDiffCallback : DiffUtil.ItemCallback<Ticket>() {
        override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
            return oldItem == newItem
        }
    }
}