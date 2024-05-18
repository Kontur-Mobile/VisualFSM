package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.kontur.mobile.visualfsm.State

internal class OwnStateKeeperRx<STATE : State>(
    initialState: STATE,
) : IStateKeeperRx<STATE> {

    @Volatile
    private var currentState = initialState
    private val stateRxObservableField = BehaviorSubject.createDefault(initialState).toSerialized()

    override fun observeState(): Observable<STATE> {
        return stateRxObservableField
    }

    override fun getCurrentState(): STATE {
        return currentState
    }

    override fun updateState(newState: STATE) {
        currentState = newState
        stateRxObservableField.onNext(newState)
    }
}