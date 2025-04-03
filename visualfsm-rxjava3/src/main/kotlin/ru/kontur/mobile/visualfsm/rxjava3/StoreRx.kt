package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Observable
import ru.kontur.mobile.visualfsm.*

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls
 * (like logging, debugging, or metrics) (optional)
 * @param stateSource the [state source][IStateSourceRx] for storing and subscribing to state,
 * can be external to implement a common state tree between parent and child state machines
 * @param transitionsFactory the [factory][TransitionsFactory] Factory for creating [transitions][Transition]
 * described in [action][Action]
 */
internal class StoreRx<STATE : State, ACTION : Action<STATE>>(
    private val stateSource: IStateSourceRx<STATE>,
    private val transitionCallbacks: TransitionCallbacks<STATE, ACTION>,
    private val transitionsFactory: TransitionsFactory<STATE, ACTION>,
) : BaseStore<STATE, ACTION>(stateSource, transitionCallbacks, transitionsFactory) {

    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    internal fun observeState(): Observable<STATE> {
        return stateSource.observeState()
    }
}