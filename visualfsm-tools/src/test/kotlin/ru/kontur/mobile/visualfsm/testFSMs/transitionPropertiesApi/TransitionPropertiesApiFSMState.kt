package ru.kontur.mobile.visualfsm.testFSMs.transitionPropertiesApi

import ru.kontur.mobile.visualfsm.State

sealed class TransitionPropertiesApiFSMState : State {

    object Initial : TransitionPropertiesApiFSMState()

    object Loaded : TransitionPropertiesApiFSMState()

    object ExtraState : TransitionPropertiesApiFSMState()

    sealed class AsyncWorkState : TransitionPropertiesApiFSMState() {

        object Loading : AsyncWorkState()

        object Updating : AsyncWorkState()
    }
}