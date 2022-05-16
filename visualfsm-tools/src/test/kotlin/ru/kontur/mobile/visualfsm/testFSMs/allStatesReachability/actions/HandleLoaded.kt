package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.AsyncWorkState.Loading
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.AsyncWorkState.Updating
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.AllStatesReachabilityFSMState.Loaded

class HandleLoaded : AllStatesReachabilityFSMAction() {

    inner class LoadingToLoadedTransition : Transition<Loading, Loaded>(
        Loading::class,
        Loaded::class
    ) {

        override fun transform(state: Loading) = Loaded
    }

    inner class UpdatingToLoadedTransition : Transition<Updating, Loaded>(
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