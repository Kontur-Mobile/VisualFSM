package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState

class NavigateToFirstScreen : BaseSealedStateFSMAction() {

    inner class InitialFirstScreen : Transition<SealedActionStateFSMState.Initial, SealedActionStateFSMState.Navigation.Screen.NextFirstScreen>() {
        override fun transform(state: SealedActionStateFSMState.Initial): SealedActionStateFSMState.Navigation.Screen.NextFirstScreen {
            return SealedActionStateFSMState.Navigation.Screen.NextFirstScreen(state)
        }
    }

    inner class AsyncWorkerStateFirstScreen :
        Transition<SealedActionStateFSMState.AsyncWorkerState, SealedActionStateFSMState.Navigation.Screen.NextFirstScreen>() {
        override fun transform(state: SealedActionStateFSMState.AsyncWorkerState): SealedActionStateFSMState.Navigation.Screen.NextFirstScreen {
            return SealedActionStateFSMState.Navigation.Screen.NextFirstScreen(state)
        }
    }
}