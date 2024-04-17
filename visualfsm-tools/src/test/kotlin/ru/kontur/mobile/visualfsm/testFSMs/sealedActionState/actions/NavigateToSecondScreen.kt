package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState

class NavigateToSecondScreen : BaseSealedStateFSMAction() {

    inner class InitialToSecondScreen : Transition<SealedActionStateFSMState.Initial, SealedActionStateFSMState.Navigation.Screen.NextSecondScreen>() {
        override fun transform(state: SealedActionStateFSMState.Initial): SealedActionStateFSMState.Navigation.Screen.NextSecondScreen {
            return SealedActionStateFSMState.Navigation.Screen.NextSecondScreen(state)
        }
    }

    inner class AsyncWorkerStateToSecondScreen :
        Transition<SealedActionStateFSMState.AsyncWorkerState, SealedActionStateFSMState.Navigation.Screen.NextSecondScreen>() {
        override fun transform(state: SealedActionStateFSMState.AsyncWorkerState): SealedActionStateFSMState.Navigation.Screen.NextSecondScreen {
            return SealedActionStateFSMState.Navigation.Screen.NextSecondScreen(state)
        }
    }
}