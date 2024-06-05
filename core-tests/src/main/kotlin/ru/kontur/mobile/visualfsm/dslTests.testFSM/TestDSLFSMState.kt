package ru.kontur.mobile.visualfsm.dslTests.testFSM

import ru.kontur.mobile.visualfsm.State

sealed class TestDSLFSMState : State {

    data class Initial(
        val count: Int,
    ) : TestDSLFSMState()

    sealed class AsyncWorkerState : TestDSLFSMState() {
        data object Loading : AsyncWorkerState()
    }

    data class Loaded(
        val count: Int,
    ) : TestDSLFSMState()
}