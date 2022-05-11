package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability

import ru.kontur.mobile.visualfsm.AsyncWorkerTaskRx
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction

class AllStatesReachabilityAsyncWorker :
    AsyncWorkerRx<AllStatesReachabilityFSMState, AllStatesReachabilityFSMAction>() {

    override fun onNextState(state: AllStatesReachabilityFSMState): AsyncWorkerTaskRx {
        return if (state !is AllStatesReachabilityFSMState.AsyncWorkState) {
            AsyncWorkerTaskRx.DisposeCurrent
        } else when (state) {
            is AllStatesReachabilityFSMState.AsyncWorkState.Loading -> TODO()
            is AllStatesReachabilityFSMState.AsyncWorkState.Updating -> TODO()
        }
    }
}