package ru.kontur.mobile.visualfsm

interface TransitionFactory<STATE : State, ACTION : Action<STATE>> {
    fun create(action: ACTION): List<Transition<out STATE, out STATE>>
}