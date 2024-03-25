package ru.kontur.mobile.visualfsm.sealedTests.testFSM

import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.TestFSMAction

@GenerateTransitionsFactory
class TestFSMFeature(
    initialState: TestFSMState,
    asyncWorker: AsyncWorker<TestFSMState, TestFSMAction>? = null,
    transitionCallbacks: TransitionCallbacks<TestFSMState>? = null,
) : Feature<TestFSMState, TestFSMAction>(
    initialState = initialState,
    asyncWorker = asyncWorker,
    transitionCallbacks = transitionCallbacks,
    transitionsFactory = provideTransitionsFactory(),
)