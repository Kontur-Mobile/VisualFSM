package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability

import ru.kontur.mobile.visualfsm.FeatureRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.Load

class AllStatesReachabilityFSMFeature(callbacks: TransitionCallbacks<AllStatesReachabilityFSMState>) : FeatureRx<AllStatesReachabilityFSMState, AllStatesReachabilityFSMAction>(
    AllStatesReachabilityFSMStore(callbacks),
    AllStatesReachabilityAsyncWorker()
) {

    fun load() {
        proceed(Load())
    }
}