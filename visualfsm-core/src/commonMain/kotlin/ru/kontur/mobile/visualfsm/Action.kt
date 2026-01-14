package ru.kontur.mobile.visualfsm

/**
 * Is an input object for the State machine.
 * The [action][Action] chooses [transition][Transition] and performs it
 */
abstract class Action<STATE : State> {
    open fun getTransitions(): List<Transition2<out STATE, out STATE>> = error("123")
}