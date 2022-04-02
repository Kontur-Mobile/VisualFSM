package authFSM

import AuthInteractor
import authFSM.actions.AuthFSMAction
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import ru.kontur.mobile.visualfsm.AsyncWorker
import authFSM.AuthFSMState.*

class AuthFSMAsyncWorker(private val authInteractor: AuthInteractor) : AsyncWorker<AuthFSMState, AuthFSMAction>() {
    private val scope = CoroutineScope(Dispatchers.Default);

    override fun initSubscription(states: Flow<AuthFSMState>): Job {
        return scope.launch {
            states.collect { state ->
                if (state !is AsyncWorkState) {
                    dispose()
                    return@collect
                }
                when (state) {
                    is AsyncWorkState.Checking -> authInteractor.check(state.mail, state.password)
                    is AsyncWorkState.Registering -> authInteractor.register(state.mail, state.password)
                }
            }
        }
    }
}