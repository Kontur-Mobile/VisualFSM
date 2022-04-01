package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability

import ru.kontur.mobile.visualfsm.StoreRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.ExtraStateReachabilityFSMAction

class ExtraStateReachabilityFSMStore(callbacks: TransitionCallbacks<ExtraStateReachabilityFSMState>) : StoreRx<ExtraStateReachabilityFSMState, ExtraStateReachabilityFSMAction>(
    ExtraStateReachabilityFSMState.Initial, callbacks
)