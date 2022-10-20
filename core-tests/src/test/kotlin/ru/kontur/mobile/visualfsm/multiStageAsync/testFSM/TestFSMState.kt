package ru.kontur.mobile.visualfsm.multiStageAsync.testFSM

import ru.kontur.mobile.visualfsm.State

sealed class TestFSMState : State {
    object Initial : TestFSMState()

    data class AsyncWithStage(
        val label: String,
        val stage: String,
        val milliseconds: Int
    ) : TestFSMState()

    data class Complete(
        val label: String
    ) : TestFSMState()

    object Error : TestFSMState()
}