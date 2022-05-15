package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.StoreRx
import ru.kontur.mobile.visualfsm.testFSM.action.TestFSMAction

class TestFSMStore : StoreRx<TestFSMState, TestFSMAction>(TestFSMState.A, TestFSMTransitionCallbacks())