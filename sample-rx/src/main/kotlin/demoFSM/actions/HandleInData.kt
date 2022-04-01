package demoFSM.actions

import ru.kontur.mobile.visualfsm.Transition
import demoFSM.DemoFSMState
import demoFSM.DemoFSMTransition

class HandleInData : DemoFSMAction() {

    inner class FinishSaving() : DemoFSMTransition<DemoFSMState.AsyncWorkState.DataIn.Saving, DemoFSMState.FinalState.DataSent>(
        DemoFSMState.AsyncWorkState.DataIn.Saving::class,
        DemoFSMState.FinalState.DataSent::class
    ) {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataIn.Saving): DemoFSMState.FinalState.DataSent {
            return DemoFSMState.FinalState.DataSent
        }
    }

    inner class ErrorSaving() : DemoFSMTransition<DemoFSMState.AsyncWorkState.DataIn.Saving, DemoFSMState.Error>(
        DemoFSMState.AsyncWorkState.DataIn.Saving::class,
        DemoFSMState.Error::class
    ) {
        override fun transform(state: DemoFSMState.AsyncWorkState.DataIn.Saving): DemoFSMState.Error {
            return DemoFSMState.Error
        }
    }

    inner class RepeatError() : DemoFSMTransition<DemoFSMState.Error, DemoFSMState.Error>(
        DemoFSMState.Error::class,
        DemoFSMState.Error::class
    ) {
        override fun transform(state: DemoFSMState.Error): DemoFSMState.Error {
            return DemoFSMState.Error
        }
    }

    override val transitions: List<Transition<out DemoFSMState, out DemoFSMState>>
        get() = listOf(
            FinishSaving(),
            ErrorSaving(),
            RepeatError()
        )
}