package ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMState

class HandleInData : DemoFSMAction() {

    inner class FinishSaving : Transition<DemoFSMState.AsyncWorkState.DataIn.Saving, DemoFSMState.FinalState.DataSent>() {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataIn.Saving): DemoFSMState.FinalState.DataSent {
            return DemoFSMState.FinalState.DataSent
        }
    }

    inner class ErrorSaving : Transition<DemoFSMState.AsyncWorkState.DataIn.Saving, DemoFSMState.Error>() {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataIn.Saving): DemoFSMState.Error {
            return DemoFSMState.Error
        }
    }

    inner class RepeatError : Transition<DemoFSMState.Error, DemoFSMState.Error>() {
        override fun transform(state: DemoFSMState.Error): DemoFSMState.Error {
            return DemoFSMState.Error
        }
    }
}