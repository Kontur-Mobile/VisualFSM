package demoFSM

import demoFSM.actions.DemoFSMAction
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.kontur.mobile.visualfsm.AsyncWorkerRx

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