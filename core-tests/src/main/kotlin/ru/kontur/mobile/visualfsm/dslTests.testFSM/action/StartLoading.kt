package ru.kontur.mobile.visualfsm.dslTests.testFSM.action

import ru.kontur.mobile.visualfsm.DslAction
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.dslTests.testFSM.TestDSLFSMState

class StartLoading(
    private val count: Int,
) : TestDSLAction() {

    internal fun continueLoading() = selfTransition<TestDSLFSMState.AsyncWorkerState>() transform { state -> state }

    internal fun continueLoading1() = selfTransition<TestDSLFSMState.AsyncWorkerState> { state -> state }

    @Edge("StartLoading")
    internal fun startLoadingOther() = transition<TestDSLFSMState.Initial, TestDSLFSMState.AsyncWorkerState.Loading>()
        .predicate { count == 0 }
        .transform { state -> TestDSLFSMState.AsyncWorkerState.Loading }

    @Edge("StartLoading")
    internal fun startLoadingOther1() = transition<TestDSLFSMState.Initial, TestDSLFSMState.AsyncWorkerState.Loading>()
        .transform { state -> TestDSLFSMState.AsyncWorkerState.Loading }
}