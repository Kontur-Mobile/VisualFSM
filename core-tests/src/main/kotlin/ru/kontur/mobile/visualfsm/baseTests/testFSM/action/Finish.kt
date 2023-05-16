package ru.kontur.mobile.visualfsm.baseTests.testFSM.action

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState

class Finish(val success: Boolean, val salt: String = "") : TestFSMAction() {
    inner class Success : Transition<TestFSMState.Async, TestFSMState.Complete>(
        predicate = success,
        transform = { state ->
            Thread.sleep(10)
            TestFSMState.Complete(state.label, salt)
        }
    )

    inner class Error : Transition<TestFSMState.Async, TestFSMState.Error>(
        predicate = !success,
        transform = { state ->
            Thread.sleep(10)
            TestFSMState.Error
        }
    )
}