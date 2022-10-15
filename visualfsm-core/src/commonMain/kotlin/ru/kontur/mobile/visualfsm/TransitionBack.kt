package ru.kontur.mobile.visualfsm

/**
 * Describes the transition rule between states.
 * For back to state in stack, use interfaces ToBackStack and ToBackStackNewRoot
 * for transitions that perform a transition with adding the old state to the back stack.
 * In generic contains [initial state][fromState] and [destination state][toState].
 * Defines [predicate] and [transform] functions
 */
abstract class TransitionBack<FROM : State, TO : State> : Transition<FROM, TO>() {

    /**
     * Defines requirements for the [transition][Transition] to perform
     *
     * @param currentState current state[State]
     * @param backStackState state[State] from back stack
     * @return true if the transition should be performed, otherwise false
     */
    open fun predicate(currentState: FROM, backStackState: TO?): Boolean {
        return true
    }

    /**
     * Creates a [new state][State]
     *
     * @param currentState current state[State]
     * @param backStackState state[State] from back stack
     * @return new state[State]
     */
    open fun transform(currentState: FROM, backStackState: TO): TO {
        return backStackState
    }

    @Deprecated("TransitionBack must override fun predicate(currentState: FROM, backStackState: TO)")
    override fun predicate(state: FROM): Boolean {
        throw IllegalStateException("TransitionBack must override fun predicate(currentState: FROM, backStackState: TO)")
    }

    @Deprecated("TransitionBack must override fun transform(currentState: FROM, backStackState: TO)")
    override fun transform(state: FROM): TO {
        throw IllegalStateException("TransitionBack must override fun transform(currentState: FROM, backStackState: TO)")
    }
}