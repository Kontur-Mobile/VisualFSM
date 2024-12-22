package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

data class LogParams<STATE : State, ACTION : Action<STATE>>(
    val internalLoggingEnabled: Boolean,
    val logger: Logger = StdoutLogger(),
    val tag: String? = null,
    val logFormatter: LogFormatter<STATE, ACTION> = DefaultVerboseLogFormatter(),
)