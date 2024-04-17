package ru.kontur.mobile.visualfsm.testFSMs.baseToBaseSelfTransitionActionState

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.SelfTransition

sealed class BaseToBaseSelfTransitionStateAction : Action<BaseToBaseSelfTransitionActionFSMState>() {

    class BaseToBase : BaseToBaseSelfTransitionStateAction() {
        inner class BaseToBase : SelfTransition<BaseToBaseSelfTransitionActionFSMState>() {
            override fun transform(state: BaseToBaseSelfTransitionActionFSMState): BaseToBaseSelfTransitionActionFSMState {
                return state
            }
        }
    }
}