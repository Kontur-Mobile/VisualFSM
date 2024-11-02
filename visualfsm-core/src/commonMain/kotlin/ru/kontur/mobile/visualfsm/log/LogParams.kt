package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

data class LogParams<STATE : State, ACTION : Action<STATE>>(
    val internalLoggingEnabled: Boolean,
    val logLevel: LogLevel = LogLevel.ERROR,
    val logger: Logger = StdoutLogger(),
    val tag: String? = null,
    val logFormatter: LogFormatter<STATE, ACTION> = DefaultInfoLogFormatter(),
)

enum class LogLevel {
    ERROR, // Log only errors
    INFO, // Log errors and info
    VERBOSE; // Log errors and verbose (maximum information)
}