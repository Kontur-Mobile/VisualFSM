package ru.kontur.mobile.visualfsm.testFSMs.demoFSM

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.demoFSM.actions.DemoFSMAction

class DemoFSMAsyncWorker : AsyncWorkerRx<DemoFSMState, DemoFSMAction>() {
    override fun initSubscription(states: Observable<DemoFSMState>): Disposable {
        return states.subscribe(
            { state ->
                if (state !is DemoFSMState.AsyncWorkState) {
                    dispose()
                    return@subscribe
                }
            },
            {}
        )
    }
}