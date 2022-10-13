package ru.kontur.mobile.visualfsm

/**
 * Inherit to declare third party logic on provided event calls (like logging, debugging, or metrics)
 */
interface TransitionCallbacks<STATE : State> {

    /**
     * Is called when [Action] is being launched
     *
     * @param action [Action] that is being launched
     * @param currentState current [state][STATE]
     */
    fun onActionLaunched(
        action: Action<STATE>,
        currentState: STATE
    )

    /**
     * Is called on transition being selected
     *
     * @param action [Action] that is being launched
     * @param transition selected [transition][Transition]
     * @param currentState current [state][STATE]
     */
    fun onTransitionSelected(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    )

    /**
     * Is called when [new state][State] reduced
     *
     * @param action [Action] that is being launched
     * @param transition selected [transition][Transition]
     * @param oldState current [state][STATE]
     * @param newState new [state][State]
     */
    fun onNewStateReduced(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    )

    /**
     * Is called when [state][State] is restored from back stack
     *
     * @param oldState current [state][STATE]
     * @param newState new [state][State]
     */
    fun onRestoredFromBackStack(
        oldState: STATE,
        newState: STATE
    ) {
        // Default implementation for back compatibility
    }

    /**
     * Is called when there is no available [transition][Transition]
     *
     * @param action [Action] that was being launched
     * @param currentState current [state][State]
     */
    fun onNoTransitionError(
        action: Action<STATE>,
        currentState: STATE
    )

    /**
     * Is called when more than one [transition][Transition] is available
     *
     * @param action [Action] that was being launched
     * @param currentState current [state][State]
     */
    fun onMultipleTransitionError(
        action: Action<STATE>,
        currentState: STATE
    )
}