package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.backStack.ToBackStack

sealed class TestFSMState : State {
    object Initial : TestFSMState(), ToBackStack

    data class Async(
        val label: String,
        val milliseconds: Int
    ) : TestFSMState()

    data class Complete(
        val label: String
    ) : TestFSMState()

    object Error : TestFSMState()
}