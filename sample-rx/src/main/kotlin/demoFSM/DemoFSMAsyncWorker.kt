package demoFSM

import demoFSM.actions.DemoFSMAction
import ru.kontur.mobile.visualfsm.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.AsyncWorkerRx

class DemoFSMAsyncWorker : AsyncWorkerRx<DemoFSMState, DemoFSMAction>() {
    override fun onNextState(state: DemoFSMState): AsyncWorkerTaskRx {
        return if (state !is DemoFSMState.AsyncWorkState) {
            AsyncWorkerTaskRx.DisposeCurrent
        } else when (state) {
            DemoFSMState.AsyncWorkState.DataOut.Finding -> TODO()
            DemoFSMState.AsyncWorkState.DataOut.Loading -> TODO()
            DemoFSMState.AsyncWorkState.DataIn.Saving -> TODO()
        }
    }
}