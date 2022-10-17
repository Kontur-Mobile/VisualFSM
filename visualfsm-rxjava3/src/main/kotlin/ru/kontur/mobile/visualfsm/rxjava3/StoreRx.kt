package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.kontur.mobile.visualfsm.*
import ru.kontur.mobile.visualfsm.backStack.BackStateStack
import ru.kontur.mobile.visualfsm.store.BaseStore

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 * @param stateDependencyManager state dependency manager [StateDependencyManager]
 * @param backStateStack back state stack [BackStateStack]
 */
class StoreRx<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE,
    private val transitionCallbacks: TransitionCallbacks<STATE>?,
    stateDependencyManager: StateDependencyManager<STATE>?,
    backStateStack: BackStateStack<STATE>,
) : BaseStore<STATE, ACTION>(initialState, transitionCallbacks, stateDependencyManager, backStateStack) {

    @Volatile
    private var _currentState = initialState
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
    override fun getCurrentState(): STATE {
        return _currentState
    }

    /**
     * Set state from back stack
     *
     * @param id state id
     * @param newState restored state[State]
     */
    override fun setState(id: String, newState: STATE) {
        if (newState != _currentState) {
            _currentState = newState
            currentStateId = id
            stateRxObservableField.onNext(newState)
        }
    }
}