package ru.kontur.mobile.visualfsm

/**
 * Inherit to declare third party logic on provided event calls (like logging, debugging, or metrics)
 */
interface TransitionCallbacks<STATE : State, ACTION : Action<STATE>> {

    /**
     * Is called when the [Feature] is initialized
     *
     * @param initialState the [state][STATE] with which the Feature is initialized
     */
    fun onInitialStateReceived(
        initialState: STATE
    ) {}

    /**
     * Is called when [Action] is being launched
     *
     * @param action [Action] that is being launched
     * @param currentState current [state][STATE]
     */
    fun onActionLaunched(
        action: ACTION,
        currentState: STATE
    ) {}

    /**
     * Is called on transition being selected
     *
     * @param action [Action] that is being launched
     * @param transition selected [transition][Transition]
     * @param currentState current [state][STATE]
     */
    fun onTransitionSelected(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    ) {}

    /**
     * Is called when [new state][State] reduced
     *
     * @param action [Action] that is being launched
     * @param transition selected [transition][Transition]
     * @param oldState current [state][STATE]
     * @param newState new [state][State]
     */
    fun onNewStateReduced(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    ) {}

    /**
     * Is called when there is no available [transition][Transition]
     *
     * @param action [Action] that was being launched
     * @param currentState current [state][State]
     */
    fun onNoTransitionError(
        action: ACTION,
        currentState: STATE,
    ) {}

    /**
     * Is called when more than one [transition][Transition] is available
     *
     * @param action [Action] that was being launched
     * @param currentState current [state][State]
     * @param suitableTransitions list of suitable [transitions][Transition]
     */
    fun onMultipleTransitionError(
        action: ACTION,
        currentState: STATE,
        suitableTransitions: List<Transition<STATE, STATE>>
    ) {}
}