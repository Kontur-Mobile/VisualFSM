package ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.ExtraStateReachabilityFSMAction
import ru.kontur.mobile.visualfsm.testFSMs.extraStateReachability.actions.HandleLoaded

class ExtraStateReachabilityAsyncWorker : AsyncWorkerRx<ExtraStateReachabilityFSMState, ExtraStateReachabilityFSMAction>() {

    override fun initSubscription(states: Observable<ExtraStateReachabilityFSMState>): Disposable {
        return states.subscribe(
            { state ->
                if (state !is ExtraStateReachabilityFSMState.AsyncWorkState) {
                    dispose()
                    return@subscribe
                }

                when (state) {
                    is ExtraStateReachabilityFSMState.AsyncWorkState.Loading,
                    is ExtraStateReachabilityFSMState.AsyncWorkState.Updating -> proceed(HandleLoaded())
                }
            },
            {}
        )
    }
}