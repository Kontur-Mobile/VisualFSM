package ru.kontur.mobile.visualfsm.rxjava2

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionCallbacks

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
internal class StoreRx<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE,
    private val transitionCallbacks: TransitionCallbacks<STATE>?
) {

    @Volatile
    private var currentState = initialState
    private val stateRxObservableField = BehaviorSubject.createDefault(initialState).toSerialized()

    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    internal fun observeState(): Observable<STATE> {
        return stateRxObservableField
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    internal fun getCurrentState(): STATE {
        return currentState
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    internal fun proceed(action: ACTION) {
        val newState = reduce(action, currentState)
        val changed = newState != currentState
        if (changed) {
            currentState = newState
        }
        stateRxObservableField.onNext(newState)
    }

    /**
     * Runs [action's][Action] transition of [states][State]
     *
     * @param action launched [action][Action]
     * @param state new [state][State]
     * @return new [state][State]
     */
    private fun reduce(
        action: ACTION,
        state: STATE
    ): STATE {
        return action.run(state, transitionCallbacks)
    }
}