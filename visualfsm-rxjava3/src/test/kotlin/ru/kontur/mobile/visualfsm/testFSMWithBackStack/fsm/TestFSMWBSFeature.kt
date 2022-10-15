package ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm

import ru.kontur.mobile.visualfsm.*
import ru.kontur.mobile.visualfsm.backStack.BackStackStrategy
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.TestFSMAction

@GenerateTransitionsFactory
class TestFSMWBSFeature(
    initialState: TestFSMWBSState,
    asyncWorker: AsyncWorker<TestFSMWBSState, TestFSMAction>? = null,
    transitionCallbacks: TransitionCallbacks<TestFSMWBSState>? = null,
    stateDependencyManager: StateDependencyManager<TestFSMWBSState>? = null
) : Feature<TestFSMWBSState, TestFSMAction>(
    initialState = initialState,
    initialStateAddToBackStackStrategy = BackStackStrategy.ADD,
    asyncWorker = asyncWorker,
    transitionCallbacks = transitionCallbacks,
    transitionsFactory = provideTransitionsFactory(),
    stateDependencyManager = stateDependencyManager,
)