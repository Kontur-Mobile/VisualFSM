package ru.kontur.mobile.visualfsm.backStack

import ru.kontur.mobile.visualfsm.State
import kotlin.reflect.KClass

/**
 * BackStateStack provides a Last In First Out (LIFO) states[State] with id [StateWithId].
 * @param elements - elements for restore stack
 */
class BackStateStack<STATE : State>(elements: List<StateWithId<STATE>> = listOf()) {
    private val backStatesDeque: ArrayDeque<StateWithId<STATE>> = ArrayDeque(elements)

    /**
     * Pushes a state with id[StateWithId] onto the top of the stack.
     *
     * @param stateWithId the state with id to push onto the stack
     */
    fun push(stateWithId: StateWithId<STATE>) {
        backStatesDeque.addLast(stateWithId)
    }

    /**
     * Pops a StateWithId
     *
     * @return the Pair with StateWithId popped from the stack and list of removed StateWithId
     */
    fun popAndGetRemoved(stateClass: KClass<STATE>): Pair<StateWithId<STATE>, List<StateWithId<STATE>>> {
        val stateWithId = peek(stateClass)
        val skippedStates = mutableListOf<StateWithId<STATE>>()

        if (stateWithId == null) {
            throw IllegalStateException("No states found in back stack")
        }

        do {
            val lastStateWithId = backStatesDeque.last()
            skippedStates.add(lastStateWithId)
            backStatesDeque.removeLast()
        } while (lastStateWithId.state::class != stateClass)


        return stateWithId to skippedStates
    }

    /**
     * Returns the top state on the stack with stateClass without removing it.
     *
     * @param stateClass - class of state
     * @return top state from the stack, or null if the stack is empty
     */
    fun peek(stateClass: KClass<STATE>): StateWithId<STATE>? {
        return backStatesDeque.lastOrNull { it.state::class == stateClass }
    }

    /**
     * Clear of the stack.
     *
     * @return removed states
     */
    fun clear(): List<StateWithId<STATE>> {
        val removedStates = backStatesDeque.toList()
        backStatesDeque.clear()
        return removedStates
    }
}