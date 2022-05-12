package ru.kontur.mobile.visualfsm.testFSMs.demoFSM

import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions.DemoFSMAction

class DemoFSMAsyncWorker : AsyncWorkerRx<DemoFSMState, DemoFSMAction>() {
    override fun onNextState(state: DemoFSMState): AsyncWorkerTaskRx<DemoFSMState> {
        return if (state !is DemoFSMState.AsyncWorkState) {
            AsyncWorkerTaskRx.Cancel()
        } else when (state) {
            DemoFSMState.AsyncWorkState.DataOut.Finding -> TODO()
            DemoFSMState.AsyncWorkState.DataOut.Loading -> TODO()
            DemoFSMState.AsyncWorkState.DataIn.Saving -> TODO()
        }
    }
}