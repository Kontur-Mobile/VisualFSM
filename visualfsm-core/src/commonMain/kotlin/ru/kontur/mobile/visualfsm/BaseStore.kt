package ru.kontur.mobile.visualfsm

/**
 * Stores current [state][State] and proceeds an [actions][Action].
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param stateSource the [state source][IBaseStateSource] for storing and getting state,
 * can be external to implement a common state tree between parent and child state machines
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
 * on provided event calls (like logging, debugging, or metrics)
 * @param transitionsFactory the [factory][TransitionsFactory] Factory for creating [transitions][Transition]
 * described in [action][Action]
 */
abstract class BaseStore<STATE : State, ACTION : Action<STATE>>(
    private val stateSource: IBaseStateSource<STATE>,
    private val transitionCallbacks: TransitionCallbacks<STATE, ACTION>,
    private val transitionsFactory: TransitionsFactory<STATE, ACTION>,
) {

    init {
        transitionCallbacks.onInitialStateReceived(stateSource.getCurrentState())
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE {
        return stateSource.getCurrentState()
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    fun proceed(action: ACTION) {
        val newState = reduce(action, getCurrentState())
        stateSource.updateState(newState)
    }

    /**
     * Runs [action][Action]
     *
     * @param action launched [action][Action]
     * @param oldState old state [state][State]
     * @return new [state][State]
     */
    private fun reduce(
        action: ACTION, oldState: STATE
    ): STATE {
        transitionCallbacks.onActionLaunched(action, oldState)

        val availableTransitions = getAvailableTransitions(action, oldState)

        if (availableTransitions.size > 1) {
            transitionCallbacks.onMultipleTransitionError(
                action,
                oldState,
                availableTransitions
            )
        }

        val selectedTransition = availableTransitions.firstOrNull()

        if (selectedTransition == null) {
            transitionCallbacks.onNoTransitionError(action, oldState)
            return oldState
        }

        transitionCallbacks.onTransitionSelected(action, selectedTransition, oldState)

        val newState = selectedTransition.transform(oldState)

        transitionCallbacks.onNewStateReduced(action, selectedTransition, oldState, newState)

        return newState
    }

    @Suppress("UNCHECKED_CAST")
    private fun getAvailableTransitions(action: ACTION, oldState: STATE): List<Transition<STATE, STATE>> =
        (transitionsFactory.create(action) as List<Transition<STATE, STATE>>).filter {
            isCorrectTransition(
                transition = it,
                oldState = oldState
            )
        }

    private fun isCorrectTransition(
        transition: Transition<STATE, STATE>,
        oldState: STATE,
    ): Boolean = (transition.fromState == oldState::class) && transition.predicate(oldState)
}
