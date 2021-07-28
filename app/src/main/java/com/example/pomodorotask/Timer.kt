package com.example.pomodorotask

data class Timer(
    var id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var startPeriod: Long
)