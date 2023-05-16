package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Loading
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.AsyncWorkState.Updating
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.Initial
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.ExtraStateReachabilityFSMState.Loaded

class Load : ExtraStateReachabilityFSMAction() {

    inner class InitialToLoadingTransition : Transition<Initial, Loading>(transform = Loading)

    inner class LoadingToLoadingTransition : Transition<Loading, Loading>() {

        override fun transform(state: Loading) = state
    }

    inner class LoadedToUpdatingTransition : Transition<Loaded, Updating>(transform = Updating)

    inner class UpdatingToUpdatingTransition : Transition<Updating, Updating>() {

        override fun transform(state: Updating) = state
    }
}
