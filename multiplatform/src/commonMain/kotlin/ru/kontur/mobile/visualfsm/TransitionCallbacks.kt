package ru.kontur.mobile.visualfsm

interface TransitionCallbacks<STATE : State> {

    fun onActionLaunched(
        action: Action<STATE>,
        currentState: STATE
    )

    fun onTransitionSelected(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    )

    fun onNewStateReduced(
        action: Action<STATE>,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    )

    fun onNoTransitionError(
        action: Action<STATE>,
        currentState: STATE
    )

    fun onMultipleTransitionError(
        action: Action<STATE>,
        currentState: STATE
    )
}