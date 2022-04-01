package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions

import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.AsyncWorkState.Loading
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.AsyncWorkState.Updating
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.Initial
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.Loaded
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMTransition

class Load : AllStatesReachabilityFSMAction() {

    inner class InitialToLoadingTransition : AllStatesReachabilityFSMTransition<Initial, Loading>(
        Initial::class,
        Loading::class
    ) {

        override fun transform(state: Initial) = Loading
    }

    inner class LoadingToLoadingTransition : AllStatesReachabilityFSMTransition<Loading, Loading>(
        Loading::class,
        Loading::class
    ) {

        override fun transform(state: Loading) = state
    }

    inner class LoadedToUpdatingTransition : AllStatesReachabilityFSMTransition<Loaded, Updating>(
        Loaded::class,
        Updating::class
    ) {

        override fun transform(state: Loaded) = Updating
    }

    inner class UpdatingToUpdatingTransition : AllStatesReachabilityFSMTransition<Updating, Updating>(
        Updating::class,
        Updating::class
    ) {

        override fun transform(state: Updating) = state
    }

    override val transitions = listOf(
        InitialToLoadingTransition(),
        LoadingToLoadingTransition(),
        LoadedToUpdatingTransition(),
        UpdatingToUpdatingTransition()
    )
}
