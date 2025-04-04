package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

/**
 * Interface for formatting state and action objects into a string representation.
 *
 * @param <STATE> The type representing the base state.
 * @param <ACTION> The type representing the base action.
 */
interface LogFormatters<STATE : State, ACTION : Action<STATE>> {

    /**
     * Formats the given state into a string representation.
     *
     * @param state The state to format.
     * @return A string representation of the state, or null if not needed to log message with this state.
     */
    fun formatState(state: STATE): String?

    /**
     * Formats the given action into a string representation.
     *
     * @param action The action to format.
     * @return A string representation of the action, or null if not needed to log message with this action.
     */
    fun formatAction(action: ACTION): String?
}
