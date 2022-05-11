package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability

import ru.kontur.mobile.visualfsm.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.ExtraStateReachabilityFSMAction

class ExtraStateReachabilityAsyncWorker :
    AsyncWorkerRx<ExtraStateReachabilityFSMState, ExtraStateReachabilityFSMAction>() {

    override fun onNextState(state: ExtraStateReachabilityFSMState): AsyncWorkerTaskRx {
        return if (state !is ExtraStateReachabilityFSMState.AsyncWorkState) {
            AsyncWorkerTaskRx.DisposeCurrent
        } else when (state) {
            is ExtraStateReachabilityFSMState.AsyncWorkState.Loading -> TODO()
            is ExtraStateReachabilityFSMState.AsyncWorkState.Updating -> TODO()
        }
    }
}