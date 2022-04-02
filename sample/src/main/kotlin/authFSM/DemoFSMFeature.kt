package authFSM

import AuthInteractor
import authFSM.actions.AuthFSMAction
import authFSM.actions.Auth
import authFSM.actions.StartRegistration
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class DemoFSMFeature(callbacks: TransitionCallbacks<AuthFSMState>) :
    Feature<AuthFSMState, AuthFSMAction>(AuthFSMStore(callbacks), AuthFSMAsyncWorker(AuthInteractor())) {
    fun auth() {
        proceed(Auth())
    }

    fun registration() {
        proceed(StartRegistration())
    }
}