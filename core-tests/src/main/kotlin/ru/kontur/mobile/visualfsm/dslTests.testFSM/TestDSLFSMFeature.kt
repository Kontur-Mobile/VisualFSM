package ru.kontur.mobile.visualfsm.dslTests.testFSM

import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.dslTests.testFSM.action.TestDSLAction
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory

@GenerateTransitionsFactory
class TestDSLFSMFeature(
    initialState: TestDSLFSMState,
    asyncWorker: AsyncWorker<TestDSLFSMState, TestDSLAction>? = null,
    transitionCallbacks: TransitionCallbacks<TestDSLFSMState>? = null,
) : Feature<TestDSLFSMState, TestDSLAction>(
    initialState = initialState,
    asyncWorker = asyncWorker,
    transitionCallbacks = transitionCallbacks,
    transitionsFactory = provideTransitionsFactory(),
)