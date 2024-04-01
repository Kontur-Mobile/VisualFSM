package ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions

import ru.kontur.mobile.visualfsm.SelfTransition
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState

class ObserverChange : BaseSealedStateFSMAction() {
    inner class ChangeByObserve : SelfTransition<SealedActionStateFSMState>() {
        override fun transform(state: SealedActionStateFSMState): SealedActionStateFSMState {
            return state
        }
    }
}