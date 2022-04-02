package ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.AllStatesReachabilityFSMAction
import ru.kontur.mobile.visualfsm.testFSMs.allStatesReachability.actions.HandleLoaded

class AllStatesReachabilityAsyncWorker :
    AsyncWorkerRx<AllStatesReachabilityFSMState, AllStatesReachabilityFSMAction>() {

    override fun initSubscription(states: Observable<AllStatesReachabilityFSMState>): Disposable {
        return states.subscribe({ state ->
            if (state !is AllStatesReachabilityFSMState.AsyncWorkState) {
                dispose()
                return@subscribe
            }

            when (state) {
                is AllStatesReachabilityFSMState.AsyncWorkState.Loading,
                is AllStatesReachabilityFSMState.AsyncWorkState.Updating -> proceed(HandleLoaded())
            }
        }, {})
    }
}