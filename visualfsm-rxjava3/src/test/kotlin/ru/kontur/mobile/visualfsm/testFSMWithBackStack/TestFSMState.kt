package ru.kontur.mobile.visualfsm.testFSMWithBackStack

import ru.kontur.mobile.visualfsm.State

sealed class TestFSMState : State {
    object Initial : TestFSMState()

    data class Async(
        val label: String,
        val milliseconds: Int
    ) : TestFSMState()

    data class Complete(
        val label: String
    ) : TestFSMState()

    object Error : TestFSMState()
}