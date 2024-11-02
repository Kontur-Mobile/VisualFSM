package ru.kontur.mobile.visualfsm.transitioncallbacks

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.log.LogFormatter
import ru.kontur.mobile.visualfsm.log.LogLevel
import ru.kontur.mobile.visualfsm.log.Logger

class LogTransitionCallbacks<STATE : State, ACTION : Action<STATE>>(
    private val logLevel: LogLevel,
    private val logger: Logger,
    private val tag: String,
    private val logFormatter: LogFormatter<STATE, ACTION>,
) : TransitionCallbacks<STATE> {

    override fun onInitialStateReceived(initialState: STATE) {
        if (logLevel == LogLevel.ERROR) return

        val message = "LogTransitionCallbacks onInitialStateReceived: ${logFormatter.stateFormatter(initialState)}"

        when (logLevel) {
            LogLevel.ERROR -> Unit

            LogLevel.INFO -> logger.log(
                tag = tag,
                message = message
            )

            LogLevel.VERBOSE -> logger.log(
                tag = tag,
                message = message
            )
        }
    }

    override fun onActionLaunched(
        action: Action<STATE>,
        currentState: STATE
    ) {
        logger.log(tag, "")
    }

    override fun onTransitionSelected(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    ) {
        logger.log(tag, "")
    }

    override fun onNewStateReduced(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    ) {
        logger.log(tag, "")
    }

    override fun onNoTransitionError(
        action: Action<STATE>,
        currentState: STATE,
    ) {
        logger.log(tag, "")
    }

    override fun onMultipleTransitionError(
        action: Action<STATE>,
        currentState: STATE,
    ) {
        logger.log(tag, "")
    }
}
