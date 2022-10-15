package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.*
import ru.kontur.mobile.visualfsm.backStack.BackStackStrategy
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction

@GenerateTransitionsFactory
class TestFSMFeature(
    initialState: TestFSMState,
    asyncWorker: AsyncWorker<TestFSMState, TestFSMAction>? = null,
    transitionCallbacks: TransitionCallbacks<TestFSMState>? = null,
    stateDependencyManager: StateDependencyManager<TestFSMState>? = null
) : Feature<TestFSMState, TestFSMAction>(
    initialState = initialState,
    initialStateAddToBackStackStrategy = BackStackStrategy.ADD,
    asyncWorker = asyncWorker,
    transitionCallbacks = transitionCallbacks,
    transitionsFactory = provideTransitionsFactory(),
    stateDependencyManager = stateDependencyManager,
)