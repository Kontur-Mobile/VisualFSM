package ru.kontur.mobile.visualfsm

import ru.kontur.mobile.visualfsm.backStack.*
import ru.kontur.mobile.visualfsm.uuid.UUIDStringGenerator

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
     * @param oldStateId current [state][State] identifier
     * @param callbacks [transition callbacks][TransitionCallbacks]
     * @param stateDependencyManager state dependency manager [StateDependencyManager]
     * @param backStateStack back state stack[BackStateStack]
     * @return [new state][State]
     */
    internal fun run(
        oldState: STATE,
        oldStateId: String,
        callbacks: TransitionCallbacks<STATE>?,
        stateDependencyManager: StateDependencyManager<STATE>?,
        backStateStack: BackStateStack<STATE>
    ): StateWithId<STATE> {
        callbacks?.onActionLaunched(this, oldState)

        val availableTransitions = getAvailableTransitions(oldState, backStateStack)

        if (availableTransitions.size > 1) {
            callbacks?.onMultipleTransitionError(this, oldState)
        }

        val selectedTransition = availableTransitions.firstOrNull()

        if (selectedTransition == null) {
            callbacks?.onNoTransitionError(this, oldState)
            return StateWithId(oldStateId, oldState)
        }

        callbacks?.onTransitionSelected(this, selectedTransition, oldState)

        val nextState = if (selectedTransition is TransitionBack) {
            getStateFromBackStack(
                oldState,
                oldStateId,
                selectedTransition,
                backStateStack,
                stateDependencyManager,
                callbacks
            )
        } else {
            getNextState(
                oldState,
                oldStateId,
                selectedTransition,
                stateDependencyManager,
            )
        }

        when (selectedTransition) {
            is TransitionPush -> {
                pushOldStateToStack(
                    oldStateId,
                    oldState,
                    backStateStack
                )
            }

            is TransitionClear -> {
                clearStack(
                    backStateStack,
                    stateDependencyManager
                )
            }

            else -> {
                stateDependencyManager?.removeDependencyForState(oldStateId, oldState)
            }
        }

        callbacks?.onNewStateReduced(this, selectedTransition, oldState, nextState.state)

        return nextState
    }

    private fun pushOldStateToStack(
        oldStateId: String,
        oldState: STATE,
        backStateStack: BackStateStack<STATE>,
    ) {
        backStateStack.push(StateWithId(oldStateId, oldState))
    }

    private fun clearStack(
        backStateStack: BackStateStack<STATE>,
        stateDependencyManager: StateDependencyManager<STATE>?,
    ) {
        backStateStack.clear().forEach {
            stateDependencyManager?.removeDependencyForState(it.id, it.state)
        }
    }

    private fun getNextState(
        oldState: STATE,
        oldStateId: String,
        selectedTransition: Transition<STATE, STATE>,
        stateDependencyManager: StateDependencyManager<STATE>?,
    ): StateWithId<STATE> {
        val newState = selectedTransition.transform(oldState)
        if (newState == oldState) return StateWithId(oldStateId, oldState)

        val newStateId = UUIDStringGenerator.randomUUID()
        stateDependencyManager?.initDependencyForState(newStateId, newState)

        return StateWithId(newStateId, newState)
    }

    private fun getStateFromBackStack(
        oldState: STATE,
        oldStateId: String,
        selectedTransition: TransitionBack<STATE, STATE>,
        backStateStack: BackStateStack<STATE>,
        stateDependencyManager: StateDependencyManager<STATE>?,
        callbacks: TransitionCallbacks<STATE>?,
    ): StateWithId<STATE> {
        val peekResult = backStateStack.peek(selectedTransition.toState)

        val nextStateWithId = if (peekResult == null) {
            callbacks?.onNoStateInBackStackError(selectedTransition.toState, oldState)
            StateWithId(oldStateId, oldState)
        } else {
            val (stateFromBackStack, removedStates) = backStateStack.popAndGetRemoved(selectedTransition.toState)

            removedStates.filter {
                it.id != stateFromBackStack.id
            }.forEach {
                stateDependencyManager?.removeDependencyForState(it.id, it.state)
            }

            val transformedBackStackState = selectedTransition.transform(oldState, stateFromBackStack.state)
            StateWithId(stateFromBackStack.id, transformedBackStackState)
        }

        return nextStateWithId
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun getAvailableTransitions(
        oldState: STATE,
        backStateStack: BackStateStack<STATE>
    ): List<Transition<STATE, STATE>> {
        return (getTransitions() as List<Transition<STATE, STATE>>).filter {
            isCorrectTransition(
                it,
                oldState,
                backStateStack
            )
        }
    }


    private fun isCorrectTransition(
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        backStateStack: BackStateStack<STATE>,
    ): Boolean {
        return if (transition is TransitionBack) {
            val backState = backStateStack.peek(transition.toState)?.state
            (transition.fromState == oldState::class) && transition.predicate(oldState, backState)
        } else {
            (transition.fromState == oldState::class) && transition.predicate(oldState)
        }
    }
}