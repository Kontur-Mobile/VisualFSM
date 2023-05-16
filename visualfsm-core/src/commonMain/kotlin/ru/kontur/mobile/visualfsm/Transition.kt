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
    @Deprecated(
        message = "Deprecated, because now the fromState and toState is setted in the generated code (of TransitionsFactory).\n" +
                "Code generation not configured or configured incorrectly.\n" +
                "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).",
        replaceWith = ReplaceWith("Constructor without parameters")
    )
    constructor(fromState: KClass<FROM>, toState: KClass<TO>) : this() {
        this._fromState = fromState
        this._toState = toState
    }

    /** This property is needed to use it in the generated code. Do not use it. */
    @Suppress("PropertyName")
    var _fromState: KClass<FROM>? = null

    /** This property is needed to use it in the generated code. Do not use it. */
    @Suppress("PropertyName")
    var _toState: KClass<TO>? = null

    /**
     * A [state][State] that FSM had on [transition][Transition] start
     */
    val fromState: KClass<FROM>
        get() = _fromState ?: error(
            "\nCode generation not configured or configured incorrectly.\n" +
                    "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).\n"
        )

    /**
     * A [state][State] FSM would have after the [transition][Transition] completes
     */
    val toState: KClass<TO>
        get() = _toState ?: error(
            "\nCode generation not configured or configured incorrectly.\n" +
                    "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).\n"
        )

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
    open fun transform(state: FROM): TO {
        TODO("Not implemented. Configure transform function in the Transition class by overriding fun transform(state: FROM): TO or set transform in Transition constructor")
    }

    private var _predicate: ((state: FROM) -> Boolean)? = null
    private var _transform: ((state: FROM) -> TO)? = null

    internal val transformInternal: (state: FROM) -> TO
        get() = _transform ?: ::transform

    internal val predicateInternal: (state: FROM) -> Boolean
        get() = _predicate ?: ::predicate

    constructor(transform: TO) : this() {
        this._transform = { transform }
    }

    constructor(transform: (state: FROM) -> TO) : this() {
        this._transform = transform
    }

    constructor(predicate: Boolean, transform: TO) : this() {
        this._predicate = { predicate }
        this._transform = { transform }
    }

    constructor(predicate: Boolean, transform: (state: FROM) -> TO) : this() {
        this._predicate = { predicate }
        this._transform = transform
    }

    constructor(predicate: (state: FROM) -> Boolean, transform: TO) : this() {
        this._predicate = predicate
        this._transform = { transform }
    }

    constructor(predicate: (state: FROM) -> Boolean, transform: (state: FROM) -> TO) : this() {
        this._predicate = predicate
        this._transform = transform
    }
}