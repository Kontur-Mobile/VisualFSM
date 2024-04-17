package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState

import ru.kontur.mobile.visualfsm.State

sealed class SealedActionStateFSMState : State {
    object Initial : SealedActionStateFSMState()

    sealed class AsyncWorkerState : SealedActionStateFSMState() {
        object LoadingFirst : AsyncWorkerState()
        object LoadingSecond : AsyncWorkerState()
    }

    sealed class Navigation : SealedActionStateFSMState() {
        object Back : Navigation()

        sealed class Screen : Navigation() {
            abstract val sourceState: SealedActionStateFSMState

            data class NextFirstScreen(
                override val sourceState: SealedActionStateFSMState,
            ) : Screen()

            data class NextSecondScreen(
                override val sourceState: SealedActionStateFSMState,
            ) : Screen()
        }
    }
}