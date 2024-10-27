package ru.kontur.mobile.visualfsm.transitioncallbacks

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks

/**
 * Inherit to declare third party logic on provided event calls (like logging, debugging, or metrics)
 */
interface DefaultTransitionCallbacks<STATE : State> : TransitionCallbacks<STATE> {

    /**
     * Is called when the [Feature] is initialized
     *
     * @param initialState the [state][STATE] with which the Feature is initialized
     */
    override fun onInitialStateReceived(
        initialState: STATE
    ) {
    }

    /**
     * Is called when [Action] is being launched
     *
     * @param action [Action] that is being launched
     * @param currentState current [state][STATE]
     */
    override fun onActionLaunched(
        action: Action<STATE>,
        currentState: STATE
    ) {
    }

    /**
     * Is called on transition being selected
     *
     * @param action [Action] that is being launched
     * @param transition selected [transition][Transition]
     * @param currentState current [state][STATE]
     */
    override fun onTransitionSelected(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    ) {
    }

    /**
     * Is called when [new state][State] reduced
     *
     * @param action [Action] that is being launched
     * @param transition selected [transition][Transition]
     * @param oldState current [state][STATE]
     * @param newState new [state][State]
     */
    override fun onNewStateReduced(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    ) {
    }

    /**
     * Is called when there is no available [transition][Transition]
     *
     * @param action [Action] that was being launched
     * @param currentState current [state][State]
     */
    override fun onNoTransitionError(
        action: Action<STATE>,
        currentState: STATE,
    ) {
    }

    /**
     * Is called when more than one [transition][Transition] is available
     *
     * @param action [Action] that was being launched
     * @param currentState current [state][State]
     */
    override fun onMultipleTransitionError(
        action: Action<STATE>,
        currentState: STATE,
    ) {
    }
}