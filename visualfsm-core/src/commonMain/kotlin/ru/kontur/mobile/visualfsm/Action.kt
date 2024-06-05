package ru.kontur.mobile.visualfsm

import kotlin.reflect.KClass

/**
 * Is an input object for the State machine.
 * The [action][Action] chooses [transition][Transition] and performs it
 */
abstract class Action<STATE : State> {

    private var transitions: List<Transition<out STATE, out STATE>>? = null

    /** This method is needed to use it in the generated code. Do not use it. */
    fun setTransitions(transitions: List<Transition<out STATE, out STATE>>) {
        this.transitions = transitions
    }

    /**
     * Returns instances of all [transitions][Transition] declared inside this [Action]
     *
     * @return instances of all [transitions][Transition] declared inside this [Action]
     */
    @Deprecated(
        message = "Deprecated, because now the list of transitions is formed in the generated code (of TransitionsFactory).\n" +
            "Code generation not configured or configured incorrectly.\n" +
            "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).",
    )
    open fun getTransitions(): List<Transition<out STATE, out STATE>> {
        return transitions ?: error(
            "\nCode generation not configured or configured incorrectly.\n" +
                "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).\n"
        )
    }

    /**
     * Selects and starts a [transition][Transition].
     * Calls [transition callbacks][TransitionCallbacks] methods.
     *
     * @param oldState current [state][State]
     * @param callbacks [transition callbacks][TransitionCallbacks]
     * @return [new state][State]
     */
    fun run(oldState: STATE, callbacks: TransitionCallbacks<STATE>?): STATE {
        callbacks?.onActionLaunched(this, oldState)

        val availableTransitions = getAvailableTransitions(oldState)

        if (availableTransitions.size > 1) {
            callbacks?.onMultipleTransitionError(this, oldState)
        }

        val selectedTransition = availableTransitions.firstOrNull()

        if (selectedTransition == null) {
            callbacks?.onNoTransitionError(this, oldState)
            return oldState
        }

        callbacks?.onTransitionSelected(this, selectedTransition, oldState)

        val newState = selectedTransition.transform(oldState)

        callbacks?.onNewStateReduced(this, selectedTransition, oldState, newState)

        return newState
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun getAvailableTransitions(oldState: STATE): List<Transition<STATE, STATE>> =
        (getTransitions() as List<Transition<STATE, STATE>>).filter { isCorrectTransition(it, oldState) }

    private fun isCorrectTransition(
        transition: Transition<STATE, STATE>,
        oldState: STATE,
    ): Boolean =
        (transition.fromState == oldState::class) && transition.predicate(oldState)
}

abstract class DslAction<STATE : State> : Action<STATE>(), DslTransitionAction<STATE>, DslSelfTransitionAction<STATE>

interface DslSelfTransitionAction<STATE : State> {

    infix fun <TRANSITION_STATE : STATE> KClass<TRANSITION_STATE>.selfTransition(
        function: (TRANSITION_STATE) -> TRANSITION_STATE,
    ): SelfTransition<TRANSITION_STATE> {
        return object : SelfTransition<TRANSITION_STATE>() {
            override fun transform(state: TRANSITION_STATE): TRANSITION_STATE {
                return function(state)
            }
        }
    }

    fun <TRANSITION_STATE : STATE> selfTransition(): Builder<TRANSITION_STATE> {
        return Builder()
    }

    fun <TRANSITION_STATE : STATE> selfTransition(
        predicate: (TRANSITION_STATE) -> Boolean = { true },
        transform: (TRANSITION_STATE) -> TRANSITION_STATE,
    ): SelfTransition<TRANSITION_STATE> {
        return object : SelfTransition<TRANSITION_STATE>() {
            override fun predicate(state: TRANSITION_STATE) = predicate(state)
            override fun transform(state: TRANSITION_STATE) = transform(state)
        }
    }

    fun <TRANSITION_STATE : STATE> selfTransition(
        predicate: Boolean,
        transform: (TRANSITION_STATE) -> TRANSITION_STATE,
    ): Transition<TRANSITION_STATE, TRANSITION_STATE> = selfTransition(predicate = { predicate }, transform = transform)

    class Builder<STATE : State> {
        var predicate: (STATE) -> Boolean = { true }

        infix fun predicate(
            function: (STATE) -> Boolean,
        ): Builder<STATE> {
            return this.apply { predicate = function }
        }

        infix fun transform(
            function: (STATE) -> STATE,
        ): SelfTransition<STATE> {
            return object : SelfTransition<STATE>() {
                override fun predicate(state: STATE): Boolean {
                    return this@Builder.predicate(state)
                }

                override fun transform(state: STATE): STATE {
                    return function(state)
                }
            }
        }
    }
}

interface DslTransitionAction<STATE : State> {
    fun <FROM : STATE, TO : STATE> transition(): Builder<FROM, TO> {
        return Builder()
    }

    fun <FROM : STATE, TO : STATE> transition(
        predicate: (FROM) -> Boolean = { true },
        transform: (FROM) -> TO,
    ): Transition<FROM, TO> {
        return object : Transition<FROM, TO>() {
            override fun predicate(state: FROM) = predicate(state)
            override fun transform(state: FROM) = transform(state)
        }
    }

    fun <FROM : STATE, TO : STATE> transition(
        predicate: Boolean,
        transform: (FROM) -> TO,
    ): Transition<FROM, TO> = transition(predicate = { predicate }, transform = transform)

    infix fun <FROM : STATE, TO : STATE> KClass<FROM>.transition(to: KClass<TO>): Builder<FROM, TO> {
        return Builder()
    }

    class Builder<FROM : State, TO : State> {
        var predicate: (FROM) -> Boolean = { true }

        infix fun predicate(function: (FROM) -> Boolean): Builder<FROM, TO> {
            return this.apply { predicate = function }
        }

        infix fun transform(function: (FROM) -> TO): Transition<FROM, TO> {
            return object : Transition<FROM, TO>() {
                override fun predicate(state: FROM): Boolean {
                    return this@Builder.predicate(state)
                }

                override fun transform(state: FROM): TO {
                    return function(state)
                }
            }
        }
    }
}