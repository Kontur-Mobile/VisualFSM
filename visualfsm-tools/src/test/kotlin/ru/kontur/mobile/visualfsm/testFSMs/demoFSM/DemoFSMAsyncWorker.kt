package ru.kontur.mobile.visualfsm.testFSMs.demoFSM

import ru.kontur.mobile.visualfsm.AsyncWorkStrategyRx
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions.DemoFSMAction

class DemoFSMAsyncWorker : AsyncWorkerRx<DemoFSMState, DemoFSMAction>() {
    override fun onNextState(state: DemoFSMState): AsyncWorkStrategyRx {
        return if (state !is DemoFSMState.AsyncWorkState) {
            AsyncWorkStrategyRx.DisposeCurrent
        } else when (state) {
            DemoFSMState.AsyncWorkState.DataOut.Finding -> TODO()
            DemoFSMState.AsyncWorkState.DataOut.Loading -> TODO()
            DemoFSMState.AsyncWorkState.DataIn.Saving -> TODO()
        }
    }
}