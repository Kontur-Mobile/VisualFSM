package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions

import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Loading
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Updating
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.Initial
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.Loaded
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMTransition

class Load : ExtraStateReachabilityFSMAction() {

    inner class InitialToLoadingTransition : ExtraStateReachabilityFSMTransition<Initial, Loading>(
        Initial::class,
        Loading::class
    ) {

        override fun transform(state: Initial) = Loading
    }

    inner class LoadingToLoadingTransition : ExtraStateReachabilityFSMTransition<Loading, Loading>(
        Loading::class,
        Loading::class
    ) {

        override fun transform(state: Loading) = state
    }

    inner class LoadedToUpdatingTransition : ExtraStateReachabilityFSMTransition<Loaded, Updating>(
        Loaded::class,
        Updating::class
    ) {

        override fun transform(state: Loaded) = Updating
    }

    inner class UpdatingToUpdatingTransition : ExtraStateReachabilityFSMTransition<Updating, Updating>(
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
