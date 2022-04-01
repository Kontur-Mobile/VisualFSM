package demoFSM

import demoFSM.actions.DemoFSMAction
import demoFSM.actions.HandleOutData
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class DemoFSMFeature(callbacks: TransitionCallbacks<DemoFSMState>) : Feature<DemoFSMState, DemoFSMAction>(DemoFSMStore(callbacks), DemoFSMAsyncWorker()) {
    fun outData() {
        proceed(HandleOutData())
    }

    fun inData() {
        proceed(HandleOutData())
    }
}