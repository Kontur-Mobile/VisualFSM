package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Loading
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Updating
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.Loaded

class HandleLoaded : ExtraStateReachabilityFSMAction() {

    inner class LoadingToLoadedTransition : Transition<Loading, Loaded>() {

        override fun transform(state: Loading) = Loaded
    }

    inner class UpdatingToLoadedTransition : Transition<Updating, Loaded>() {

        override fun transform(state: Updating) = Loaded
    }
}