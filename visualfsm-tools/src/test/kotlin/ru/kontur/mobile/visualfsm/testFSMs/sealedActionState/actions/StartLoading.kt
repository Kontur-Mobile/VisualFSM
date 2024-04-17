package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState

class StartLoading : BaseSealedStateFSMAction() {

    inner class InitialToFirstLoading : Transition<SealedActionStateFSMState.Initial, SealedActionStateFSMState.AsyncWorkerState.LoadingFirst>() {
        override fun transform(state: SealedActionStateFSMState.Initial): SealedActionStateFSMState.AsyncWorkerState.LoadingFirst {
            return SealedActionStateFSMState.AsyncWorkerState.LoadingFirst
        }
    }

    inner class InitialToSecondLoading :
        Transition<SealedActionStateFSMState.AsyncWorkerState.LoadingFirst, SealedActionStateFSMState.AsyncWorkerState.LoadingSecond>() {
        override fun transform(state: SealedActionStateFSMState.AsyncWorkerState.LoadingFirst): SealedActionStateFSMState.AsyncWorkerState.LoadingSecond {
            return SealedActionStateFSMState.AsyncWorkerState.LoadingSecond
        }
    }
}