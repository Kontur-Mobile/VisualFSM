package ru.kontur.mobile.visualfsm.testFSMWithBackStack.action

import ru.kontur.mobile.visualfsm.TransitionBack
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.TestFSMState

class Close : TestFSMAction() {

    inner class Close : TransitionBack<TestFSMState.Complete, TestFSMState.Initial>()
}