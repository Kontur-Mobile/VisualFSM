package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

/**
 * Describes the transition rule between states.
 * In generic contains [initial state][fromState] and [destination state][toState].
 * Defines [predicate] and [transform] functions
 */
abstract class Transition<FROM : State, TO : State>() {

    /**
     * @param fromState a [state][State] that FSM had on [transition][Transition] start
     * @param toState a [state][State] FSM would have after the [transition][Transition] completes
     */
    @Deprecated(message = "") // TODO Add message to annotation
    constructor(fromState: KClass<FROM>, toState: KClass<TO>) : this() {
        this.fromState = fromState
        this.toState = toState
    }

    /**
     * A [state][State] that FSM had on [transition][Transition] start
     */
    lateinit var fromState: KClass<FROM>

    /**
     * A [state][State] FSM would have after the [transition][Transition] completes
     */
    lateinit var toState: KClass<TO>

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