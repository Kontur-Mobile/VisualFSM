package ru.kontur.mobile.visualfsm

/** Factory for creating [transitions][Transition] described in [action][Action] */
interface TransitionFactory<STATE : State, ACTION : Action<STATE>> {

    /**
     * Returns a list of [transitions][Transition] described in [action][Action]
     *
     * @param action [Action] for which need a list of [transitions][Transition]
     * @return a list of [transitions][Transition] described in [action][Action]
     */
    fun create(action: ACTION): List<Transition<out STATE, out STATE>>
}