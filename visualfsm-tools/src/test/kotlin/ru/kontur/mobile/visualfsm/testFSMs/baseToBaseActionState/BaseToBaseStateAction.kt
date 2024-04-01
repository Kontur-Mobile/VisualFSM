package ru.kontur.mobile.visualfsm.testFSMs.baseToBaseActionState

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition

sealed class BaseToBaseStateAction : Action<BaseToBaseActionFSMState>() {

    class BaseToBase : BaseToBaseStateAction() {
        inner class BaseToBase : Transition<BaseToBaseActionFSMState, BaseToBaseActionFSMState>() {
            override fun transform(state: BaseToBaseActionFSMState): BaseToBaseActionFSMState {
                return state
            }
        }
    }
}