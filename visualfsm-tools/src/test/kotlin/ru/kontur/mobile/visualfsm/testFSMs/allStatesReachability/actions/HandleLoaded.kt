package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions

import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.AsyncWorkState.Loading
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.AsyncWorkState.Updating
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.Loaded
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMTransition

class HandleLoaded : AllStatesReachabilityFSMAction() {

    inner class LoadingToLoadedTransition : AllStatesReachabilityFSMTransition<Loading, Loaded>(
        Loading::class,
        Loaded::class
    ) {

        override fun transform(state: Loading) = Loaded
    }

    inner class UpdatingToLoadedTransition : AllStatesReachabilityFSMTransition<Updating, Loaded>(
        Updating::class,
        Loaded::class
    ) {

        override fun transform(state: Updating) = Loaded
    }

    override fun getTransitions() = listOf(
        LoadingToLoadedTransition(),
        UpdatingToLoadedTransition()
    )
}