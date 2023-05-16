package ru.kontur.mobile.visualfsm.baseTests.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState

class Start(val label: String, val milliseconds: Int, val salt: String = "") : TestFSMAction() {
    inner class Start : Transition<TestFSMState.Initial, TestFSMState.Async>(transform = { state ->
        Thread.sleep(10)
        TestFSMState.Async(label, milliseconds, salt)
    })

    inner class StartOther : Transition<TestFSMState.Async, TestFSMState.Async>(transform = { state ->
        Thread.sleep(10)
        TestFSMState.Async(label, milliseconds, salt)
    })
}