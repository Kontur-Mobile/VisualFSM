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
        message = "Deprecated, because now the fromState and toState is setted in the generated code (of TransitionFactory).\n" +
                "For enable code generation:\n" +
                "  1. Use annotation processor and tools dependencies in module gradle script\n." +
                "  2. Add generated code to source code directories.\n" +
                "  3. Annotate the Feature class with the GenerateTransitionFactory annotation.\n" +
                "  4. Pass the transitionFactory parameter to the Feature constructor.\n" +
                "Please see the readme file (https://github.com/g0rd1/VisualFSM/blob/g0rd1/code-generation/docs/eng/Quickstart-ENG.md) for detailed information on set up code generation.",
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
            "Code generation not configured or configured incorrectly.\n" +
                    "For enable code generation:\n" +
                    "  1. Use annotation processor and tools dependencies in module gradle script\n." +
                    "  2. Add generated code to source code directories.\n" +
                    "  3. Annotate the Feature class with the GenerateTransitionFactory annotation.\n" +
                    "  4. Pass the transitionFactory parameter to the Feature constructor.\n" +
                    "Please see the readme file (https://github.com/g0rd1/VisualFSM/blob/g0rd1/code-generation/docs/eng/Quickstart-ENG.md) for detailed information on set up code generation."
        )

    /**
     * A [state][State] FSM would have after the [transition][Transition] completes
     */
    val toState: KClass<TO>
        get() = _toState ?: error(
            "Code generation not configured or configured incorrectly.\n" +
                    "For enable code generation:\n" +
                    "  1. Use annotation processor and tools dependencies in module gradle script\n." +
                    "  2. Add generated code to source code directories.\n" +
                    "  3. Annotate the Feature class with the GenerateTransitionFactory annotation.\n" +
                    "  4. Pass the transitionFactory parameter to the Feature constructor.\n" +
                    "Please see the readme file (https://github.com/g0rd1/VisualFSM/blob/g0rd1/code-generation/docs/eng/Quickstart-ENG.md) for detailed information on set up code generation."
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
    abstract fun transform(state: FROM): TO
}