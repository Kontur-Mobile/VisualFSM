package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionCallbacks

/**
 * Logging parameters for the built-in logger, if the capabilities of the built-in logger are not enough,
 * use the [callbacks][TransitionCallbacks] to implement your own logging and LoggerMode.NONE.
 * @param loggerMode - [LoggerMode] (NONE - for disable internal logging, ERRORS(default) - log errors only, VERBOSE - log all events)
 * @param logger - custom [Logger] implementation, by default used internal [StdoutLogger]
 * @param tag - custom tag for log messages, if null - used Feature class simple name
 * @param logFormatters - custom [LogFormatters] implementation, by default used [DefaultVerboseLogFormatters]
 */
open class LogParams<STATE : State, ACTION : Action<STATE>>(
    val loggerMode: LoggerMode,
    val logger: Logger = StdoutLogger(),
    val tag: String? = null,
    val logFormatters: LogFormatters<STATE, ACTION> = DefaultVerboseLogFormatters(),
)
