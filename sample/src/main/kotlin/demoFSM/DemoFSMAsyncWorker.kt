package demoFSM

import demoFSM.actions.DemoFSMAction
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import ru.kontur.mobile.visualfsm.AsyncWorker

class DemoFSMAsyncWorker : AsyncWorker<DemoFSMState, DemoFSMAction>() {
    val scope = CoroutineScope(Dispatchers.Default);

    override fun initSubscription(states: Flow<DemoFSMState>): Job {
        return scope.launch {
            states.collect { state ->
                if (state !is DemoFSMState.AsyncWorkState) {
                    dispose()
                    return@collect
                }
                println(state)
            }
        }
    }
}