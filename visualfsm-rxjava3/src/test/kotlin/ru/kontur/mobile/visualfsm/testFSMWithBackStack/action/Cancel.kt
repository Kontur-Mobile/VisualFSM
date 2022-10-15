package ru.kontur.mobile.visualfsm.testFSMWithBackStack.action

import ru.kontur.mobile.visualfsm.TransitionBack
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMState

class Cancel : TestFSMAction() {

    inner class Cancel : TransitionBack<TestFSMState.Async, TestFSMState.Initial>()
}