package ru.kontur.mobile.visualfsm.sealedTests.testFSM

import ru.kontur.mobile.visualfsm.State

sealed class TestFSMState : State {
    object Initial : TestFSMState()

    sealed class NavigationState : TestFSMState() {

        sealed class Screen : NavigationState() {
            object Back : Screen()
            object Next : Screen()
        }

        sealed class DialogState : NavigationState() {
            object Show : DialogState()
            object Hide : DialogState()
        }
    }
}