package demoFSM

import demoFSM.actions.DemoFSMAction
import ru.kontur.mobile.visualfsm.StoreRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class DemoFSMStore(callbacks: TransitionCallbacks<DemoFSMState>) : StoreRx<DemoFSMState, DemoFSMAction>(
    DemoFSMState.Initial,
    callbacks
)