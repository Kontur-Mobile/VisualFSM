package ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action

import ru.kontur.mobile.visualfsm.backStack.TransitionBack
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState

class Close : TestFSMAction() {

    inner class Close : TransitionBack<TestFSMWBSState.Complete, TestFSMWBSState.Initial>()
}