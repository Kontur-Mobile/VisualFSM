package ru.kontur.mobile.visualfsm

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.reflect.KClass

/**
 * BaseFeature - common code for Feature and FeatureRx
 *
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 * @param dependencyManager state dependency manager [StateDependencyManager]
 * @param restoredBackStates list Pairs id and state for restored back state stack */
abstract class BaseFeature<STATE : State, ACTION : Action<STATE>>(
    private val dependencyManager: StateDependencyManager<STATE>?,
    private val transitionCallbacks: TransitionCallbacks<STATE>?,
    restoredBackStates: List<Pair<Int, STATE>>,
) : SynchronizedObject() {

    init {
        dependencyManager?.let {
            restoredBackStates.forEach { (id, state) ->
                it.initDependencyForState(id, state)
            }
        }
    }

    protected abstract val store: BaseStore<STATE, ACTION>
    protected val backStatesStack = BackStateStack(restoredBackStates)

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    abstract fun proceed(action: ACTION)

    /**
     * Restore back state[State] from stack.
     * Use back() only if you need to restore the state without changing it,
     * to restore with a change, use back(state: State)
     *
     * @return true[Boolean] if stack is not empty, else false[Boolean]
     */
    fun back(): Boolean {
        synchronized(this) {
            val stateWithId = backStatesStack.popWithId() ?: return false
            val (id, backState) = stateWithId
            performBack(id, backState)
            return true
        }
    }

    /**
     * Restore updated back state[State] from stack with update.
     * Use back() without arguments if you need to restore the state without changes
     *
     * @return true[Boolean] if stack is not empty and state class is the same as state from backStack class else false[Boolean]
     */
    fun back(state: STATE): Boolean {
        synchronized(this) {
            val stateWithId = backStatesStack.peekWithId() ?: return false
            val (id, backState) = stateWithId

            return if (backState::class == state::class) {
                backStatesStack.removeLast()
                performBack(id, state)
                true
            } else {
                false
            }
        }
    }

    fun backTo(stateClass: KClass<STATE>): Boolean {
        return false // ToDo call remove all dependency skipped states
    }

    fun backTo(state: STATE): Boolean {
        return false // ToDo call remove all dependency skipped states
    }

    fun peekStateFromBackStack(): STATE? {
        return backStatesStack.peek()
    }

    private fun performBack(id: Int, backState: STATE) {
        val oldState = store.getCurrentState()
        val oldStateId = store.currentStateId
        store.setState(id, backState)
        dependencyManager?.removeDependencyForState(oldStateId, oldState)
        transitionCallbacks?.onRestoredFromBackStack(oldState, backState)
    }

    companion object
}