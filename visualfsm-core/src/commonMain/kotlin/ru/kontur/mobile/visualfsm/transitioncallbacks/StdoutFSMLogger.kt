package ru.kontur.mobile.visualfsm.transitioncallbacks

class StdoutFSMLogger(private val logLevel: LogLevel) : FSMLogger {
    override fun i(message: String) {
        if (logLevel == LogLevel.INFO) {
            println("StdoutFSMLogger i: $message")
        }
    }

    override fun v(message: String) {
        if (logLevel == LogLevel.VERBOSE) {
            println("StdoutFSMLogger v: $message")
        }
    }

    override fun e(message: String, exception: Exception, errorGroupId: String) {
        println("StdoutFSMLogger e: $message")
        println("StdoutFSMLogger errorGroupId = $errorGroupId")
        println("StdoutFSMLogger exception message = ${exception.message}")
        println("StdoutFSMLogger exception stacktrace = ${exception.stackTraceToString()}")
    }
}