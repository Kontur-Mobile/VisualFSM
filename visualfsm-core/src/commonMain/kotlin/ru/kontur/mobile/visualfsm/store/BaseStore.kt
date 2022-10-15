package ru.kontur.mobile.visualfsm.store

import ru.kontur.mobile.visualfsm.*
import ru.kontur.mobile.visualfsm.backStack.BackStateStack

/**
 * BaseStore
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 * @param stateDependencyManager state dependency manager [StateDependencyManager]
 * @param backStateStack back state stack [BackStateStack]
 */
abstract class BaseStore<STATE : State, ACTION : Action<STATE>>(
    private val initialState: STATE,
    private val transitionCallbacks: TransitionCallbacks<STATE>?,
    private val stateDependencyManager: StateDependencyManager<STATE>?,
    private val backStateStack: BackStateStack<STATE>,
) {

    var currentStateId: Int = 0

    init {
        stateDependencyManager?.initDependencyForState(currentStateId, initialState)
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    abstract fun getCurrentState(): STATE

    /**
     * Set state
     *
     * @param id state[State] id
     * @param newState new state[State]
     * @return true is state[State] changed
     */
    abstract fun setState(id: Int, newState: STATE)

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     * @return state[State] if state changed, or null
     */
    fun proceed(action: ACTION) {
        val currentState = getCurrentState()
        val newState = reduce(action, currentState)
        if (currentState != newState) {
            currentStateId++
            stateDependencyManager?.initDependencyForState(currentStateId, newState)
        } else {
            currentStateId
        }
        setState(currentStateId, newState)
    }

    /**
     * Runs [action's][Action] transition of [states][State]
     *
     * @param action launched [action][Action]
     * @param state new [state][State]
     * @return new [state][State]
     */
    private fun reduce(
        action: ACTION, state: STATE
    ): STATE {
        return action.run(state, currentStateId, transitionCallbacks, stateDependencyManager, backStateStack)
    }
}