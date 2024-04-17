package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState

class NavigateBack : BaseSealedStateFSMAction() {
    inner class InitialToBack : Transition<SealedActionStateFSMState.Initial, SealedActionStateFSMState.Navigation.Back>() {
        override fun transform(state: SealedActionStateFSMState.Initial): SealedActionStateFSMState.Navigation.Back {
            return SealedActionStateFSMState.Navigation.Back
        }
    }

    inner class AsyncWorkerStateToBack : Transition<SealedActionStateFSMState.AsyncWorkerState, SealedActionStateFSMState.Navigation.Back>() {
        override fun transform(state: SealedActionStateFSMState.AsyncWorkerState): SealedActionStateFSMState.Navigation.Back {
            return SealedActionStateFSMState.Navigation.Back
        }
    }
}