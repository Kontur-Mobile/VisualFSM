package ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState

class Start(val label: String, val milliseconds: Int) : TestFSMAction() {

    inner class Start : Transition<TestFSMWBSState.Initial, TestFSMWBSState.Async>() {
        override fun transform(state: TestFSMWBSState.Initial): TestFSMWBSState.Async {
            Thread.sleep(30)
            return TestFSMWBSState.Async(label, milliseconds)
        }
    }

    inner class StartOther : Transition<TestFSMWBSState.Async, TestFSMWBSState.Async>() {
        override fun transform(state: TestFSMWBSState.Async): TestFSMWBSState.Async {
            Thread.sleep(30)
            return TestFSMWBSState.Async(label, milliseconds)
        }
    }
}