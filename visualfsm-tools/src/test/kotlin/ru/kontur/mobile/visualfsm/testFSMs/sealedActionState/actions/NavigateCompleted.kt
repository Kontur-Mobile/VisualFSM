package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState

class NavigateCompleted : BaseSealedStateFSMAction() {

    inner class NavigateCompletedToInitial : Transition<SealedActionStateFSMState.Navigation.Screen, SealedActionStateFSMState.Initial>() {
        override fun predicate(state: SealedActionStateFSMState.Navigation.Screen): Boolean {
            return state.sourceState is SealedActionStateFSMState.Initial
        }

        override fun transform(state: SealedActionStateFSMState.Navigation.Screen): SealedActionStateFSMState.Initial {
            return state.sourceState as SealedActionStateFSMState.Initial
        }
    }

    inner class NavigateCompletedToAsyncWorker :
        Transition<SealedActionStateFSMState.Navigation.Screen, SealedActionStateFSMState.AsyncWorkerState>() {
        override fun predicate(state: SealedActionStateFSMState.Navigation.Screen): Boolean {
            return state.sourceState is SealedActionStateFSMState.AsyncWorkerState
        }

        override fun transform(state: SealedActionStateFSMState.Navigation.Screen): SealedActionStateFSMState.AsyncWorkerState {
            return state.sourceState as SealedActionStateFSMState.AsyncWorkerState
        }
    }
}