package com.example.pomodorotask
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.isInvisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodorotask.databinding.PomodoroItemBinding
class TimerVIewHolder(
    private val binding: PomodoroItemBinding,
    private val listener: TimerListener,
): RecyclerView.ViewHolder(binding.root) {
    private var tiimer: CountDownTimer? = null
    fun bind(timer: Timer) {
        binding.stopwatchTimer.text = timer.currentMs.displayTime()
        binding.stopwatchTimer.text = timer.currentMs.displayTime()
        binding.customViewTwo.setPeriod(timer.startPeriod)
        binding.customViewTwo.setCurrent(timer.startPeriod - timer.currentMs)
        if (timer.isStarted) {
            startTimer(timer)
        } else {
            stopTimer(timer)
        }

        initButtonsListeners(timer)
    }

    private fun initButtonsListeners(stopwatch:Timer) {
        binding.startStopButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id,stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }
        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch:Timer) {
        binding.startStopButton.text = "Stop"

        tiimer?.cancel()
        tiimer = getCountDownTimer(stopwatch)
        tiimer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch:Timer) {
        binding.startStopButton.text = "Start"

        tiimer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch:Timer): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMs, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS
            override fun onTick(millisUntilFinished: Long) {
               stopwatch.currentMs = millisUntilFinished
                binding.customViewTwo.setCurrent(stopwatch.startPeriod - millisUntilFinished)
                binding.stopwatchTimer.text =stopwatch.currentMs.displayTime()
            }
            override fun onFinish() {
                binding.stopwatchTimer.text ="finished"
                binding.stopwatchTimer.setBackgroundColor(2)
                Toast.makeText(itemView.context, "Timer finished!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00"
        private const val UNIT_TEN_MS = 19L
        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day
    }
}