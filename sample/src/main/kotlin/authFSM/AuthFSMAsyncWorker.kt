package authFSM

import AuthInteractor
import authFSM.actions.AuthFSMAction
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import ru.kontur.mobile.visualfsm.AsyncWorker
import authFSM.AuthFSMState.*
import authFSM.actions.HandleAuthResult
import authFSM.actions.HandleRegistrationResult

class AuthFSMAsyncWorker(private val authInteractor: AuthInteractor) : AsyncWorker<AuthFSMState, AuthFSMAction>() {
    override val taskScope = CoroutineScope(Dispatchers.IO)
    override val subscriptionScope = CoroutineScope(Dispatchers.Default)

    override suspend fun initSubscription(states: Flow<AuthFSMState>) {
        states.collect { state ->
            if (state !is AsyncWorkState) {
                dispose()
                return@collect
            }
            when (state) {
                is AsyncWorkState.Authenticating -> {
                    executeIfNotExist(state) {
                        val result = authInteractor.check(state.mail, state.password)
                        proceed(HandleAuthResult(result))
                    }
                }
                is AsyncWorkState.Registering -> {
                    executeIfNotExist(state) {
                        val result = authInteractor.register(state.mail, state.password)
                        proceed(HandleRegistrationResult(result))
                    }
                }
            }
        }
    }
}