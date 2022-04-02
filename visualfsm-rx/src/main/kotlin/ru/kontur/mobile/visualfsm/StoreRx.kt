package ru.kontur.mobile.visualfsm

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

abstract class StoreRx<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE,
    private val transitionCallbacks: TransitionCallbacks<STATE>
) {

    private var currentState = initialState
    private val stateRxObservableField = BehaviorSubject.createDefault(initialState).toSerialized()

    internal fun observeState(): Observable<STATE> {
        return stateRxObservableField
    }

    @Synchronized
    internal fun getStateSingle(): Single<STATE> {
        return Single.fromCallable { currentState }
    }

    @Synchronized
    internal fun proceed(action: ACTION) {
        val newState = reduce(action, currentState)
        val changed = newState != currentState
        if (changed) {
            currentState = newState
            stateRxObservableField.onNext(newState)
        }
    }

    private fun reduce(
        action: ACTION,
        state: STATE
    ): STATE {
        return action.run(state, transitionCallbacks)
    }
}