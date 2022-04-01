package demoFSM.actions

import ru.kontur.mobile.visualfsm.Transition
import demoFSM.DemoFSMState
import demoFSM.DemoFSMTransition

class HandleOutData : DemoFSMAction() {

    inner class FinishLoading() : DemoFSMTransition<DemoFSMState.AsyncWorkState.DataOut.Loading, DemoFSMState.FinalState.DataReceived>(
        DemoFSMState.AsyncWorkState.DataOut.Loading::class,
        DemoFSMState.FinalState.DataReceived::class
    ) {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataOut.Loading): DemoFSMState.FinalState.DataReceived {
            return DemoFSMState.FinalState.DataReceived
        }
    }

    inner class FinishFinding() : DemoFSMTransition<DemoFSMState.AsyncWorkState.DataOut.Finding, DemoFSMState.FinalState.DataReceived>(
        DemoFSMState.AsyncWorkState.DataOut.Finding::class,
        DemoFSMState.FinalState.DataReceived::class
    ) {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataOut.Finding): DemoFSMState.FinalState.DataReceived {
            return DemoFSMState.FinalState.DataReceived
        }
    }

    override val transitions: List<Transition<out DemoFSMState, out DemoFSMState>>
        get() = listOf(
            FinishLoading(),
            FinishFinding(),
        )
}