package ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.actions

import ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.TransitionPropertiesApiFSMState.AsyncWorkState
import ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.TransitionPropertiesApiFSMState.Initial
import ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi.TransitionPropertiesApiFSMState.Loaded

class Load : TransitionPropertiesApiFSMAction() {

    val initialToLoadingTransition = transition<Initial, AsyncWorkState.Loading> { AsyncWorkState.Loading }

    val loadingToLoadingTransition = transition<AsyncWorkState.Loading, AsyncWorkState.Loading> { state -> state }

    val loadedToUpdatingTransition = transition<Loaded, AsyncWorkState.Updating> { AsyncWorkState.Updating }

    val updatingToUpdatingTransition = transition<AsyncWorkState.Updating, AsyncWorkState.Updating> { state -> state }
}