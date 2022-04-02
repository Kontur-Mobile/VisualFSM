package ru.kontur.mobile.visualfsm.testFSMs.demoFSM

import ru.kontur.mobile.visualfsm.FeatureRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions.DemoFSMAction

class DemoFSMFeature(callbacks: TransitionCallbacks<DemoFSMState>) : FeatureRx<DemoFSMState, DemoFSMAction>(DemoFSMStore(callbacks), DemoFSMAsyncWorker())