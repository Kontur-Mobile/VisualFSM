package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability

import ru.kontur.mobile.visualfsm.AsyncWorkStrategyRx
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.ExtraStateReachabilityFSMAction

class ExtraStateReachabilityAsyncWorker :
    AsyncWorkerRx<ExtraStateReachabilityFSMState, ExtraStateReachabilityFSMAction>() {

    override fun onNextState(state: ExtraStateReachabilityFSMState): AsyncWorkStrategyRx {
        return if (state !is ExtraStateReachabilityFSMState.AsyncWorkState) {
            AsyncWorkStrategyRx.DisposeCurrent
        } else when (state) {
            is ExtraStateReachabilityFSMState.AsyncWorkState.Loading -> TODO()
            is ExtraStateReachabilityFSMState.AsyncWorkState.Updating -> TODO()
        }
    }
}