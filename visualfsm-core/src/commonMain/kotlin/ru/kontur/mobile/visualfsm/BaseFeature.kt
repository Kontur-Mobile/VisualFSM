package ru.kontur.mobile.visualfsm

abstract class BaseFeature<STATE : State, ACTION : Action<STATE>> {

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    abstract fun getCurrentState(): STATE

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    abstract fun proceed(action: ACTION)

    companion object
}