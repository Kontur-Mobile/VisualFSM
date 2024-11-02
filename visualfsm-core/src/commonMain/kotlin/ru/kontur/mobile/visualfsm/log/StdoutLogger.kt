package ru.kontur.mobile.visualfsm.log

class StdoutLogger : Logger {
    override fun log(tag: String, message: String) {
        println("StdoutFSMLogger $tag: $message")
    }

    override fun error(tag: String, message: String, exception: Exception, errorGroupId: String) {
        println("StdoutFSMLogger $tag e: $message")
        println("StdoutFSMLogger $tag errorGroupId = $errorGroupId")
        println("StdoutFSMLogger $tag exception message = ${exception.message}")
        println("StdoutFSMLogger $tag exception stacktrace = ${exception.stackTraceToString()}")
    }
}
