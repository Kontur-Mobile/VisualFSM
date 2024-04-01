package ru.kontur.mobile.visualfsm.testFSMs.baseToBaseActionState

import ru.kontur.mobile.visualfsm.State

sealed class BaseToBaseActionFSMState : State {

    object Initial : BaseToBaseActionFSMState()

    sealed class AsyncWorkerState : BaseToBaseActionFSMState() {
        sealed class Remote : AsyncWorkerState() {
            object LoadingFirst : Remote()

            object LoadingSecond : Remote()
        }
    }

    sealed class Navigation : BaseToBaseActionFSMState() {
        object NextScreen : Navigation()
    }
}