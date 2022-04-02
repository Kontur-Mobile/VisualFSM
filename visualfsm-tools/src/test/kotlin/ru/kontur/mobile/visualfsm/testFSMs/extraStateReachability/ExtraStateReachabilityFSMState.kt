package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability

import ru.kontur.mobile.visualfsm.State

sealed class ExtraStateReachabilityFSMState : State {

    object Initial : ExtraStateReachabilityFSMState()

    object Loaded : ExtraStateReachabilityFSMState()

    object ExtraState : ExtraStateReachabilityFSMState()

    sealed class AsyncWorkState : ExtraStateReachabilityFSMState() {

        object Loading : AsyncWorkState()

        object Updating : AsyncWorkState()
    }
}