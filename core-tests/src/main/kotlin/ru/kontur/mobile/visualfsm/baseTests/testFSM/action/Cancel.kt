package ru.kontur.mobile.visualfsm.baseTests.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState

class Cancel : TestFSMAction() {

    inner class Cancel : Transition<TestFSMState.Async, TestFSMState.Initial>(transform = TestFSMState.Initial)
}