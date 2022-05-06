package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

/**
 * Describes the transition rule between states.
 * In generic contains [initial state][fromState] and [destination state][toState].
 * Defines [predicate] and [transform] functions
 *
 * @param fromState a [state][State] that FSM had on [transition][Transition] start
 * @param toState a [state][State] FSM would have after the [transition][Transition] completes
 */
abstract class Transition<FROM : State, TO : State>(
    open val fromState: KClass<FROM>,
    open val toState: KClass<TO>,
) {

    /**
     * Defines requirements for the [transition][Transition] to perform
     *
     * @param state current [state][State]
     * @return true if the transition should be performed, otherwise false
     */
    open fun predicate(state: FROM): Boolean {
        return true
    }

    /**
     * Creates a [new state][State]
     *
     * @param state current [state][State]
     * @return new [state][State]
     */
    abstract fun transform(state: FROM): TO
}