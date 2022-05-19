package authFSM

import AuthInteractor
import authFSM.AuthFSMState.AsyncWorkState
import authFSM.actions.AuthFSMAction
import authFSM.actions.HandleAuthResult
import authFSM.actions.HandleRegistrationResult
import ru.kontur.mobile.visualfsm.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.AsyncWorkerTaskRx

class AuthFSMAsyncWorker(private val authInteractor: AuthInteractor) : AsyncWorkerRx<AuthFSMState, AuthFSMAction>() {
    override fun onNextState(state: AuthFSMState): AsyncWorkerTaskRx<AuthFSMState> {
        return when (state) {
            is AsyncWorkState.Authenticating -> {
                AsyncWorkerTaskRx.ExecuteAndCancelExist(state) {
                    authInteractor.check(state.mail, state.password)
                        .subscribe({
                            proceed(HandleAuthResult(it))
                        }, {
                            proceed(HandleAuthResult(AuthResult.NO_INTERNET))
                        })
                }
            }
            is AsyncWorkState.Registering -> {
                AsyncWorkerTaskRx.ExecuteIfNotExist(state) {
                    authInteractor.register(state.mail, state.password)
                        .subscribe({
                            proceed(HandleRegistrationResult(it))
                        }, {
                            proceed(HandleRegistrationResult(RegistrationResult.NO_INTERNET))
                        })
                }
            }
            else -> AsyncWorkerTaskRx.Cancel()
        }
    }
}