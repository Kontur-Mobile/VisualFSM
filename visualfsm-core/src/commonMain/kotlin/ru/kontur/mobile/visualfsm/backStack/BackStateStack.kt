package ru.kontur.mobile.visualfsm.backStack

import ru.kontur.mobile.visualfsm.State
import kotlin.reflect.KClass

/**
 * BackStateStack provides a Last In First Out (LIFO) states[State] with id [StateWithId].
 * @param elements - elements for restore stack
 */
class BackStateStack<STATE: State>(elements: List<StateWithId<STATE>> = listOf()) {
    private val backStatesDeque: ArrayDeque<StateWithId<STATE>> = ArrayDeque(elements)

    /**
     * Pushes a state and id onto the top of the stack.
     *
     * @param stateWithId the state with id to push onto the stack
     * @return removed states if newRoot is true
     */
    fun pushAndGetRemoved(stateWithId: StateWithId<STATE>, newRoot: Boolean): List<StateWithId<STATE>> {
        val removedStates = if (newRoot) {
            val allStates = backStatesDeque.toList()
            backStatesDeque.clear()
            allStates
        } else {
            listOf()
        }
        backStatesDeque.addLast(stateWithId)
        return removedStates
    }

    /**
     * Pops a StateWithId
     *
     * @return the Pair with StateWithId popped from the stack and list of removed StateWithId, or null if the stack is empty or stateClass not found
     */
    fun popAndGetRemoved(stateClass: KClass<STATE>): Pair<StateWithId<STATE>, List<StateWithId<STATE>>> {
        val stateWithId = peek(stateClass)
        val skippedStates = mutableListOf<StateWithId<STATE>>()
        if (stateWithId == null) {
            throw IllegalStateException("No states found in back stack")
        }

        for (i in backStatesDeque.size downTo 1) {
            val lastStateWithId = backStatesDeque.last()
            if (lastStateWithId.state::class != stateClass) {
                skippedStates.add(lastStateWithId)
            }
            backStatesDeque.removeLast()
        }

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
}