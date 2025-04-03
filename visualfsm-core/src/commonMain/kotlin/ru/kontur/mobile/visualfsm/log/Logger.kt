package ru.kontur.mobile.visualfsm.log

/**
 * Interface for logging internal FSM messages and errors.
 */
interface Logger {

    /**
     * Logs a message with the specified tag.
     *
     * @param tag     The tag associated with the log message (set in [LogParams])
     * @param message The message to be logged.
     */
    fun log(tag: String, message: String)

    /**
     * Logs an error message with the specified tag and an optional error group ID.
     *
     * @param tag          The tag associated with the log message (set in [LogParams])
     * @param message      The error message to be logged.
     * @param errorGroupId An optional identifier used for grouping related errors (useful for collecting metrics). Default is null.
     */
    fun error(tag: String, message: String, errorGroupId: String)
}