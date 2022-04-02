package ru.kontur.mobile.visualfsm.testFSMs.demoFSM

import ru.kontur.mobile.visualfsm.StoreRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions.DemoFSMAction

class DemoFSMStore(callbacks: TransitionCallbacks<DemoFSMState>) : StoreRx<DemoFSMState, DemoFSMAction>(
    DemoFSMState.Initial, callbacks
)