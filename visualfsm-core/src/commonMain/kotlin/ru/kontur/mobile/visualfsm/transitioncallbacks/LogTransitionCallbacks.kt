package ru.kontur.mobile.visualfsm.transitioncallbacks

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.log.LogFormatters
import ru.kontur.mobile.visualfsm.log.Logger
import ru.kontur.mobile.visualfsm.log.LoggerMode
import ru.kontur.mobile.visualfsm.log.LoggerMode.*

class LogTransitionCallbacks<STATE : State, ACTION : Action<STATE>>(
    private val loggerMode: LoggerMode,
    private val logger: Logger,
    private val tag: String,
    private val logFormatters: LogFormatters<STATE, ACTION>,
) : TransitionCallbacks<STATE, ACTION> {

    override fun onInitialStateReceived(initialState: STATE) {
        when (loggerMode) {
            NONE,
            ERRORS -> Unit

            INFO,
            VERBOSE ->
                logFormatters.formatState(initialState)?.let {
                    logger.log(
                        tag = tag,
                        message = "onInitialStateReceived: $it"
                    )
                }
        }
    }

    override fun onActionLaunched(
        action: ACTION,
        currentState: STATE
    ) {
        when (loggerMode) {
            NONE,
            ERRORS -> Unit

            INFO,
            VERBOSE ->
                logFormatters.formatAction(action)?.let {
                    logger.log(
                        tag = tag,
                        message = "onActionLaunched: $it " +
                                "from state ${currentState::class.simpleName}"
                    )
                }
        }
    }

    override fun onTransitionSelected(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    ) {
        when (loggerMode) {
            NONE,
            ERRORS,
            INFO -> Unit

            VERBOSE -> logger.log(
                tag = tag,
                message = "onTransitionSelected: ${transition::class.simpleName}, " +
                        "for action ${action::class.simpleName}"
            )
        }
    }

    override fun onNewStateReduced(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    ) {
        when (loggerMode) {
            NONE,
            ERRORS -> Unit

            INFO,
            VERBOSE ->
                logFormatters.formatState(newState)?.let {
                    logger.log(
                        tag = tag,
                        message = "onNewStateReduced: $it"
                    )
                }
        }
    }

    override fun onNoTransitionError(
        action: ACTION,
        currentState: STATE,
    ) {
        if (loggerMode == NONE) return

        logger.error(
            tag = tag,
            message = "NoTransitionError for ${logFormatters.formatAction(action) ?: action::class.simpleName} " +
                    "from ${logFormatters.formatState(currentState) ?: currentState::class.simpleName}",
            errorGroupId = "to${action::class.simpleName}" +
                    "from${currentState::class.simpleName}"
        )
    }

    override fun onMultipleTransitionError(
        action: ACTION,
        currentState: STATE,
        suitableTransitions: List<Transition<STATE, STATE>>
    ) {
        if (loggerMode == NONE) return

        logger.error(
            tag = tag,
            message = "MultipleTransitionError for ${logFormatters.formatAction(action) ?: action::class.simpleName} " +
                    "from ${logFormatters.formatState(currentState) ?: currentState::class.simpleName}, " +
                    "suitableTransitions [${
                        suitableTransitions.map { it::class.simpleName }.joinToString()
                    }]",
            errorGroupId = "suitable${suitableTransitions.size}" +
                    "to${action::class.simpleName}" +
                    "from${currentState::class.simpleName}"
        )
    }
}
