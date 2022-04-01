package ru.kontur.mobile.visualfsm

interface Action<STATE : State> {

    val transitions: List<Transition<out STATE, out STATE>>

    fun run(oldState: STATE, callbacks: TransitionCallbacks<STATE>): STATE {
        callbacks.onActionLaunched(this, oldState)

        val availableTransitions = getAvailableTransitions(oldState)

        if (availableTransitions.size > 1) {
            callbacks.onMultipleTransitionError(this, oldState)
        }

        val selectedTransition = availableTransitions.firstOrNull()

        if (selectedTransition == null) {
            callbacks.onNoTransitionError(this, oldState)
            return oldState
        }

        callbacks.onTransitionSelected(this, selectedTransition, oldState)

        val newState = selectedTransition.transform(oldState)

        callbacks.onNewStateReduced(this, selectedTransition, oldState, newState)

        return newState
    }

    @Suppress("UNCHECKED_CAST")
    private fun getAvailableTransitions(oldState: STATE): List<Transition<STATE, STATE>> =
        (transitions as List<Transition<STATE, STATE>>).filter { isCorrectTransition(it, oldState) }

    private fun isCorrectTransition(transition: Transition<STATE, STATE>, oldState: STATE): Boolean =
        (transition.fromState == oldState::class) && transition.predicate(oldState)
}