package ru.kontur.mobile.visualfsm.feature

import kotlinx.atomicfu.locks.SynchronizedObject
import ru.kontur.mobile.visualfsm.*
import ru.kontur.mobile.visualfsm.backStack.BackStateStack
import ru.kontur.mobile.visualfsm.backStack.StateWithId
import ru.kontur.mobile.visualfsm.store.BaseStore

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
    protected val backStatesStack = BackStateStack(restoredBackStates.map { StateWithId(it.first, it.second) })

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

    companion object
}