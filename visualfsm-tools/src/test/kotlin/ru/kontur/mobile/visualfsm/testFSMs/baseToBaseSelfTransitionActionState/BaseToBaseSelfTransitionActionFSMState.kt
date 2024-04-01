package ru.kontur.mobile.visualfsm.testFSMs.baseToBaseSelfTransitionActionState

import ru.kontur.mobile.visualfsm.State

sealed class BaseToBaseSelfTransitionActionFSMState : State {

    object Initial : BaseToBaseSelfTransitionActionFSMState()

    sealed class AsyncWorkerState : BaseToBaseSelfTransitionActionFSMState() {
        sealed class Remote : AsyncWorkerState() {
            object LoadingFirst : Remote()

            object LoadingSecond : Remote()
        }

        sealed class Cache : AsyncWorkerState() {
            object LoadingFirst : Cache()

            object LoadingSecond : Cache()
        }

        object Loading : AsyncWorkerState()
    }

    sealed class Navigation : BaseToBaseSelfTransitionActionFSMState() {
        object NextScreen : Navigation()

        object BackScreen : Navigation()
    }
}