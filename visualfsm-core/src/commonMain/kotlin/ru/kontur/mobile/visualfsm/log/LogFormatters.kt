package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

interface LogFormatters<STATE : State, ACTION : Action<STATE>> {

    fun formatState(state: STATE): String?

    fun formatAction(action: ACTION): String?
}
