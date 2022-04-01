package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability

import ru.kontur.mobile.visualfsm.State

sealed class AllStatesReachabilityFSMState : State {

    object Initial : AllStatesReachabilityFSMState()

    object Loaded : AllStatesReachabilityFSMState()

    sealed class AsyncWorkState : AllStatesReachabilityFSMState() {

        object Loading : AsyncWorkState()

        object Updating : AsyncWorkState()
    }
}