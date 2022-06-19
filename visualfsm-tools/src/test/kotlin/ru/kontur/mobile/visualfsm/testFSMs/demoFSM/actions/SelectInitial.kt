package ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMState

class SelectInitial : DemoFSMAction() {

    inner class StartLoadData : Transition<DemoFSMState.Initial, DemoFSMState.AsyncWorkState.DataOut.Loading>() {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.AsyncWorkState.DataOut.Loading {
            return DemoFSMState.AsyncWorkState.DataOut.Loading
        }
    }

    inner class StartFindData : Transition<DemoFSMState.Initial, DemoFSMState.AsyncWorkState.DataOut.Finding>() {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.AsyncWorkState.DataOut.Finding {
            return DemoFSMState.AsyncWorkState.DataOut.Finding
        }
    }

    inner class StartSaveData : Transition<DemoFSMState.Initial, DemoFSMState.AsyncWorkState.DataIn.Saving>() {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.AsyncWorkState.DataIn.Saving {
            return DemoFSMState.AsyncWorkState.DataIn.Saving
        }
    }

    inner class NoData : Transition<DemoFSMState.Initial, DemoFSMState.FinalInitialState>() {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.FinalInitialState {
            return DemoFSMState.FinalInitialState
        }
    }
}