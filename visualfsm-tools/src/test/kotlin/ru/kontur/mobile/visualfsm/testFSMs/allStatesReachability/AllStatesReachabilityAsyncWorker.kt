package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability

import ru.kontur.mobile.visualfsm.AsyncWorkStrategyRx
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction

class AllStatesReachabilityAsyncWorker :
    AsyncWorkerRx<AllStatesReachabilityFSMState, AllStatesReachabilityFSMAction>() {

    override fun onNextState(state: AllStatesReachabilityFSMState): AsyncWorkStrategyRx {
        return if (state !is AllStatesReachabilityFSMState.AsyncWorkState) {
            AsyncWorkStrategyRx.DisposeCurrent
        } else when (state) {
            is AllStatesReachabilityFSMState.AsyncWorkState.Loading -> TODO()
            is AllStatesReachabilityFSMState.AsyncWorkState.Updating -> TODO()
        }
    }
}