package ru.kontur.mobile.visualfsm

/** Factory for creating transitions described in action */
interface TransitionFactory<STATE : State, ACTION : Action<STATE>> {

    /**
     * Returns a list of transitions described in action
     *
     * @param action action for which need a list of transitions
     * @return a list of transitions described in action
     */
    fun create(action: ACTION): List<Transition<out STATE, out STATE>>
}