package authFSM

import AuthInteractor
import authFSM.actions.AuthFSMAction
import authFSM.actions.Authenticate
import authFSM.actions.StartRegistration
import ru.kontur.mobile.visualfsm.FeatureRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class AuthFeature(callbacks: TransitionCallbacks<AuthFSMState>) :
    FeatureRx<AuthFSMState, AuthFSMAction>(AuthFSMStore(callbacks), AuthFSMAsyncWorker(AuthInteractor())) {
    fun auth(mail: String, password: String) {
        proceed(Authenticate(mail, password))
    }

    fun registration(mail: String, password: String) {
        proceed(StartRegistration(mail, password))
    }
}