package ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMState

class HandleOutData : DemoFSMAction() {

    inner class FinishLoading : Transition<DemoFSMState.AsyncWorkState.DataOut.Loading, DemoFSMState.FinalState.DataReceived>() {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataOut.Loading): DemoFSMState.FinalState.DataReceived {
            return DemoFSMState.FinalState.DataReceived
        }
    }

    inner class FinishFinding : Transition<DemoFSMState.AsyncWorkState.DataOut.Finding, DemoFSMState.FinalState.DataReceived>() {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataOut.Finding): DemoFSMState.FinalState.DataReceived {
            return DemoFSMState.FinalState.DataReceived
        }
    }
}