package ru.kontur.mobile.visualfsm.baseTests.rx

import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory
import ru.kontur.mobile.visualfsm.rxjava2.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava2.FeatureRx
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.TestFSMAction

@GenerateTransitionsFactory
class TestFSMFeatureRx(
    initialState: TestFSMState,
    asyncWorker: AsyncWorkerRx<TestFSMState, TestFSMAction>? = null,
    transitionCallbacks: TransitionCallbacks<TestFSMState>? = null,
) : FeatureRx<TestFSMState, TestFSMAction>(
    initialState = initialState,
    asyncWorker = asyncWorker,
    transitionCallbacks = transitionCallbacks,
    transitionsFactory = provideTransitionsFactory()
)