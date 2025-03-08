package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

class DefaultInfoLogFormatters<STATE : State, ACTION : Action<STATE>>
    : LogFormatters<STATE, ACTION> {

    override fun formatState(state: STATE): String {
        return state::class.simpleName ?: "State"
    }

    override fun formatAction(action: ACTION): String {
        return action::class.simpleName ?: "Action"
    }
}