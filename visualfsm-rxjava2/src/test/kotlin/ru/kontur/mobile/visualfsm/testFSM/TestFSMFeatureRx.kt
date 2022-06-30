package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.UsesGeneratedTransactionFactory
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava2.FeatureRx
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.tools.GeneratedTransactionFactoryProvider.provideTransactionFactory

@UsesGeneratedTransactionFactory
class TestFSMFeatureRx(
    initialState: TestFSMState,
    asyncWorker: AsyncWorkerRx<TestFSMState, TestFSMAction>? = null,
    transitionCallbacks: TransitionCallbacks<TestFSMState>? = null,
) : FeatureRx<TestFSMState, TestFSMAction>(
    initialState = initialState,
    asyncWorker = asyncWorker,
    transitionCallbacks = transitionCallbacks,
    getTransitionFactory = { provideTransactionFactory() },
)