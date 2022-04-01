package ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions

import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMState
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.DemoFSMTransition

class SelectInitial : DemoFSMAction() {

    inner class StartLoadData() : DemoFSMTransition<DemoFSMState.Initial, DemoFSMState.AsyncWorkState.DataOut.Loading>(
        DemoFSMState.Initial::class,
        DemoFSMState.AsyncWorkState.DataOut.Loading::class
    ) {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.AsyncWorkState.DataOut.Loading {
            return DemoFSMState.AsyncWorkState.DataOut.Loading
        }
    }

    inner class StartFindData() : DemoFSMTransition<DemoFSMState.Initial, DemoFSMState.AsyncWorkState.DataOut.Finding>(
        DemoFSMState.Initial::class,
        DemoFSMState.AsyncWorkState.DataOut.Finding::class
    ) {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.AsyncWorkState.DataOut.Finding {
            return DemoFSMState.AsyncWorkState.DataOut.Finding
        }
    }

    inner class StartSaveData() : DemoFSMTransition<DemoFSMState.Initial, DemoFSMState.AsyncWorkState.DataIn.Saving>(
        DemoFSMState.Initial::class,
        DemoFSMState.AsyncWorkState.DataIn.Saving::class
    ) {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.AsyncWorkState.DataIn.Saving {
            return DemoFSMState.AsyncWorkState.DataIn.Saving
        }
    }

    inner class NoData() : DemoFSMTransition<DemoFSMState.Initial, DemoFSMState.FinalInitialState>(
        DemoFSMState.Initial::class,
        DemoFSMState.FinalInitialState::class
    ) {
        override fun transform(state: DemoFSMState.Initial): DemoFSMState.FinalInitialState {
            return DemoFSMState.FinalInitialState
        }
    }

    override val transitions: List<Transition<out DemoFSMState, out DemoFSMState>>
        get() = listOf(
            StartLoadData(),
            StartFindData(),
            StartSaveData()
        )
}