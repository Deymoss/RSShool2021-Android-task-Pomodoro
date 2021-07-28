package com.example.pomodorotask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodorotask.databinding.PomodoroItemBinding

class TimerAdapter(private var listener: TimerListener): ListAdapter<Timer, TimerVIewHolder>(itemComparator){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerVIewHolder  {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PomodoroItemBinding.inflate(layoutInflater, parent, false)
        return TimerVIewHolder(binding,listener)
    }

    override fun onBindViewHolder(holder: TimerVIewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Timer>() {

            override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted
            }
        }
    }
}