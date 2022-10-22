package ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm

import ru.kontur.mobile.visualfsm.State

sealed class TestFSMWBSState : State {
    object Initial : TestFSMWBSState()

    data class Async(
        val label: String,
        val milliseconds: Int
    ) : TestFSMWBSState()

    data class Complete(
        val label: String
    ) : TestFSMWBSState()

    object Error : TestFSMWBSState()
}