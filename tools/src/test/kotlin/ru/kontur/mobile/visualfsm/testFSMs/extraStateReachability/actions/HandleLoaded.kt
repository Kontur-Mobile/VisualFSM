package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions

import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Loading
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Updating
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.Loaded
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMTransition

class HandleLoaded : ExtraStateReachabilityFSMAction() {

    inner class LoadingToLoadedTransition : ExtraStateReachabilityFSMTransition<Loading, Loaded>(
        Loading::class,
        Loaded::class
    ) {

        override fun transform(state: Loading) = Loaded
    }

    inner class UpdatingToLoadedTransition : ExtraStateReachabilityFSMTransition<Updating, Loaded>(
        Updating::class,
        Loaded::class
    ) {

        override fun transform(state: Updating) = Loaded
    }

    override val transitions = listOf(
        LoadingToLoadedTransition(),
        UpdatingToLoadedTransition()
    )
}