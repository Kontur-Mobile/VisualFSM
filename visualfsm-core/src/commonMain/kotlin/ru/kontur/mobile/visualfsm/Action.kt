package ru.kontur.mobile.visualfsm

/**
 * Is an input object for the State machine.
 * The [action][Action] chooses [transition][Transition] and performs it
 */
abstract class Action<STATE : State> {

    private var transitions: List<Transition<out STATE, out STATE>>? = null

    /** This method is needed to use it in the generated code. Do not use it. */
    fun setTransitions(transitions: List<Transition<out STATE, out STATE>>) {
        this.transitions = transitions
    }

    /**
     * Returns instances of all [transitions][Transition] declared inside this [Action]
     *
     * @return instances of all [transitions][Transition] declared inside this [Action]
     */
    @Deprecated(message = "Override this method is deprecated. Pass transitionFactory parameter in Feature constructor instead.")
    open fun getTransitions(): List<Transition<out STATE, out STATE>> {
        return transitions ?: error("transitionFactory parameter not passed to Feature constructor")
    }

    /**
     * Selects and starts a [transition][Transition].
     * Calls [transition callbacks][TransitionCallbacks] methods.
     *
     * @param oldState current [state][State]
     * @param callbacks [transition callbacks][TransitionCallbacks]
     * @return [new state][State]
     */
    fun run(oldState: STATE, callbacks: TransitionCallbacks<STATE>?): STATE {
        callbacks?.onActionLaunched(this, oldState)

        val availableTransitions = getAvailableTransitions(oldState)

        if (availableTransitions.size > 1) {
            callbacks?.onMultipleTransitionError(this, oldState)
        }

        val selectedTransition = availableTransitions.firstOrNull()

        if (selectedTransition == null) {
            callbacks?.onNoTransitionError(this, oldState)
            return oldState
        }

        callbacks?.onTransitionSelected(this, selectedTransition, oldState)

        val newState = selectedTransition.transform(oldState)

        callbacks?.onNewStateReduced(this, selectedTransition, oldState, newState)

        return newState
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun getAvailableTransitions(oldState: STATE): List<Transition<STATE, STATE>> =
        (getTransitions() as List<Transition<STATE, STATE>>).filter { isCorrectTransition(it, oldState) }

    private fun isCorrectTransition(
        transition: Transition<STATE, STATE>,
        oldState: STATE,
    ): Boolean =
        (transition.fromState == oldState::class) && transition.predicate(oldState)
}