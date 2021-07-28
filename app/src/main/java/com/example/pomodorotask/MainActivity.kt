package com.example.pomodorotask

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodorotask.databinding.ActivityMainBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.android.synthetic.main.pomodoro_item.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), TimerListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private val timerAdapter = TimerAdapter(this)
    private var timers = mutableListOf<Timer>()
    private var startTime = 0L
    private var nextId = 0
    private var kostyl = "exist"
    private var mBackPressed = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        startTime = SystemClock.elapsedRealtime()
//        if(timers.isNotEmpty()) {
//            lifecycleScope.launch(Dispatchers.Main) {
//                val timer = findViewById<TextView>(R.id.stopwatch_timer)
//                while (true) {
//                    timer.text = (System.currentTimeMillis() - startTime).displayTime()
//
//                    delay(INTERVAL)
//                }
//            }
//        }
        binding.addNewTimerButton.setOnClickListener {
            if(binding.inputTime.text.toString() != "" && binding.inputTime.text.toString().toInt() > 0 &&binding.inputTime.text.toString().toInt() <= 1440  ) {
                timers.add(Timer(nextId++, binding.inputTime.text.toString().toLong()*60000, false,binding.inputTime.text.toString().toLong()*60000))
                timerAdapter.submitList(timers.toList())
            } else {
                Toast.makeText(this, "Некорректное значение, корректные от 1 минуты до 1440.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        var startedTimer = timers.find{it.isStarted}
        if (startedTimer != null) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, startedTimer.currentMs)
            startIntent.putExtra(SYSTEM_TIME, System.currentTimeMillis())
            startService(startIntent)
        }

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
    override fun start(id: Int) {
        var i =0
        while(i<timers.size)
        {
            changeTimer(i,timers[i].currentMs,false)
            i++
        }
        changeTimer(id, null, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeTimer(id, currentMs, false)
    }

    override fun delete(id: Int) {
        kostyl = "delete"
        changeTimer(id, null, false)

    }

    override fun onDestroy() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
        super.onDestroy()
    }
    private fun changeTimer(id: Int, currentMs: Long?, isStarted: Boolean) {
        var newTimers = mutableListOf<Timer>()
        if(kostyl == "delete") {//костыль нужен чтобы различать добавление/изменение с удалением
            newTimers.addAll(timers)
            newTimers.remove(newTimers.find { it.id == id })

            var i = id
            while(i!=newTimers.size)
            {
                newTimers[i].id-=1
                i++
            }
            timerAdapter.submitList(newTimers.toList())
            timers.clear()
            timers.addAll(newTimers)
            timerAdapter.notifyDataSetChanged()//ребиндит вью
            nextId--
        }else{
            timers.forEach {

                if (it.id == id) {
                    newTimers.add(Timer(it.id, currentMs ?: it.currentMs, isStarted, it.startPeriod))
                } else {
                    newTimers.add(it)
                }
            }
            timerAdapter.submitList(newTimers.toList())
            timers.clear()
            timers.addAll(newTimers)
        }
        kostyl = "exist"
    }
    private companion object {

        private const val INTERVAL = 10L
    }
    override fun onBackPressed() {
            super.onBackPressed()
        moveTaskToBack(true);
        exitProcess(-1)
    }
}