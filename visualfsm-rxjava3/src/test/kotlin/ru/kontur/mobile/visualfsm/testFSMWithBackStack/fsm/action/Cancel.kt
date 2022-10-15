package ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action

import ru.kontur.mobile.visualfsm.TransitionBack
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState

class Cancel : TestFSMAction() {

    inner class Cancel : TransitionBack<TestFSMWBSState.Async, TestFSMWBSState.Initial>()
}