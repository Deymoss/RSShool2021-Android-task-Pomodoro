package com.example.pomodorotask

    interface TimerListener {

        fun start(id: Int)

        fun stop(id: Int, currentMs: Long)

        fun delete(id: Int)
    }