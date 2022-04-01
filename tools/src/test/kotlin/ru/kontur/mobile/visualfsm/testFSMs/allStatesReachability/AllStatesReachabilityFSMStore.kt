package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability

import ru.kontur.mobile.visualfsm.StoreRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction

class AllStatesReachabilityFSMStore(callbacks: TransitionCallbacks<AllStatesReachabilityFSMState>) :
    StoreRx<AllStatesReachabilityFSMState, AllStatesReachabilityFSMAction>(
        AllStatesReachabilityFSMState.Initial, callbacks
    )