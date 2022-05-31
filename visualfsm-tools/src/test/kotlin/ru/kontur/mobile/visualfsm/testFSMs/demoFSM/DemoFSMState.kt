package ru.kontur.mobile.visualfsm.testFSMs.demoFSM

import ru.kontur.mobile.visualfsm.State

sealed class DemoFSMState : State {

    object Initial : DemoFSMState()
    object FinalInitialState : DemoFSMState()

    sealed class AsyncWorkState : DemoFSMState() {
        sealed class DataOut : AsyncWorkState() {
            object Finding : DataOut()
            object Loading : DataOut()
        }

        sealed class DataIn : AsyncWorkState() {
            object Saving : DataIn()
        }
    }

    sealed class FinalState : DemoFSMState() {
        object DataReceived : FinalState()
        object DataSent : FinalState()
    }

    object Error : DemoFSMState()
}