package ru.kontur.mobile.visualfsm.backStack

/**
 * BackStateStack provides a Last In First Out (LIFO) states[State].
 * @param elements - elements for restore stack
 */
class BackStateStack<STATE>(elements: List<Pair<Int, STATE>> = listOf()) {
    private val backStatesDeque: ArrayDeque<Pair<Int, STATE>> = ArrayDeque(elements)

    /**
     * Pushes a state and id onto the top of the stack.
     *
     * @param id the id of state to push onto the stack
     * @param state the state[State] to push onto the stack
     */
    fun push(id: Int, state: STATE) {
        backStatesDeque.addLast(Pair(id, state))
    }

    /**
     * Pops a state with id from the stack and returns it. The state popped is
     * removed from the Stack.
     *
     * @return the Pair with id and state popped from the stack, or null if the stack is empty
     */
    fun popWithId(): Pair<Int, STATE>? {
        return backStatesDeque.removeLastOrNull()
    }

    /**
     * Returns the top state with id on the stack without removing it.
     *
     * @return top the Pair with id and state from the stack, or null if the stack is empty
     */
    fun peekWithId(): Pair<Int, STATE>? {
        return backStatesDeque.lastOrNull()
    }

    /**
     * Returns the top state on the stack without removing it.
     *
     * @return top state from the stack, or null if the stack is empty
     */
    fun peek(): STATE? {
        return backStatesDeque.lastOrNull()?.second
    }

    /**
     * Remove top item from stack.
     */
    fun removeLast() {
        backStatesDeque.removeLastOrNull()
    }
}