package ru.kontur.mobile.visualfsm.transitioncallbacks

interface FSMLogger {
    // Verbose
    fun v(message: String)

    // Info
    fun i(message: String)

    // Error
    fun e(message: String, exception: Exception, errorGroupId: String)
}

enum class LogLevel {
    ERROR, // Log only errors
    INFO, // Log errors and info
    VERBOSE; // Log errors and verbose (maximum information)
}