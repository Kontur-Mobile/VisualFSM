package ru.kontur.mobile.visualfsm

/**
 * StateDependencyManager - interface for integration with application dependency management
 */
interface StateDependencyManager<STATE: State> {

    /**
     * Init dependency for state if need it.
     * @param id - unique state identifier, state[State] - state for which to initialize dependencies
     */
    fun initDependencyForState(id: String, state: STATE)

    /**
     * Remove dependency for state if need it.
     * @param id - unique state identifier, state[State] - state for which to remove dependencies
     */
    fun removeDependencyForState(id: String, state: STATE)
}