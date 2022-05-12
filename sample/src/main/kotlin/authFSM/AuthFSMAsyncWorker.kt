package authFSM

import AuthInteractor
import authFSM.actions.AuthFSMAction
import ru.kontur.mobile.visualfsm.AsyncWorker
import authFSM.AuthFSMState.*
import authFSM.actions.HandleAuthResult
import authFSM.actions.HandleRegistrationResult
import ru.kontur.mobile.visualfsm.AsyncWorkerTask

class AuthFSMAsyncWorker(private val authInteractor: AuthInteractor) : AsyncWorker<AuthFSMState, AuthFSMAction>() {
    override fun onNextState(state: AuthFSMState): AsyncWorkerTask {
        return if (state !is AsyncWorkState) {
            AsyncWorkerTask.Cancel
        } else {
            when (state) {
                is AsyncWorkState.Authenticating -> {
                    AsyncWorkerTask.ExecuteAndCancelExist(state) {
                        val result = authInteractor.check(state.mail, state.password)
                        proceed(HandleAuthResult(result))
                    }
                }
                is AsyncWorkState.Registering -> {
                    AsyncWorkerTask.ExecuteIfNotExist(state) {
                        val result = authInteractor.register(state.mail, state.password)
                        proceed(HandleRegistrationResult(result))
                    }
                }
            }
        }
    }
}