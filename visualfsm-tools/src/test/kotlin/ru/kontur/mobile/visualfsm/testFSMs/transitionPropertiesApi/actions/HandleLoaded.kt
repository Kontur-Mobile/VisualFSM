package ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.actions

import ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.TransitionPropertiesApiFSMState.AsyncWorkState
import ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.TransitionPropertiesApiFSMState.Loaded

class HandleLoaded : TransitionPropertiesApiFSMAction() {

    val loadingToLoadedTransition = transition<AsyncWorkState.Loading, Loaded> { Loaded }

    val updatingToLoadedTransition = transition<AsyncWorkState.Updating, Loaded> { Loaded }
}