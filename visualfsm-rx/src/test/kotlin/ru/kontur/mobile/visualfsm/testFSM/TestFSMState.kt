package ru.kontur.mobile.visualfsm.testFSM

import ru.kontur.mobile.visualfsm.State

sealed class TestFSMState : State {
    object A: TestFSMState()
    object B: TestFSMState()
    object C: TestFSMState()
}