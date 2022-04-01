package demoFSM

import demoFSM.actions.DemoFSMAction
import ru.kontur.mobile.visualfsm.Store
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class DemoFSMStore(callbacks: TransitionCallbacks<DemoFSMState>) : Store<DemoFSMState, DemoFSMAction>(
    DemoFSMState.Initial,
    callbacks
)