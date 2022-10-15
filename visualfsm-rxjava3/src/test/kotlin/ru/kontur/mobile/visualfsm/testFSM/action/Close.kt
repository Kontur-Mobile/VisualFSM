package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.TransitionBack
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Close : TestFSMAction() {

    inner class Close : TransitionBack<TestFSMState.Complete, TestFSMState.Initial>()
}