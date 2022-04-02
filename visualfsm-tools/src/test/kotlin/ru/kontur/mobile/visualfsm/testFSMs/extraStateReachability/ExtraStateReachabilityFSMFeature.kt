package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability

import ru.kontur.mobile.visualfsm.FeatureRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.ExtraStateReachabilityFSMAction
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.Load

class ExtraStateReachabilityFSMFeature(callbacks: TransitionCallbacks<ExtraStateReachabilityFSMState>) : FeatureRx<ExtraStateReachabilityFSMState, ExtraStateReachabilityFSMAction>(
    ExtraStateReachabilityFSMStore(callbacks),
    ExtraStateReachabilityAsyncWorker()
) {

    fun load() {
        proceed(Load())
    }
}