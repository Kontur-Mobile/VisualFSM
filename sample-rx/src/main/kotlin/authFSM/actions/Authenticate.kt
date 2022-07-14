package authFSM.actions

import authFSM.AuthFSMState.AsyncWorkState
import authFSM.AuthFSMState.Login
import ru.kontur.mobile.visualfsm.Transition

class Authenticate(val mail: String, val password: String) : AuthFSMAction() {
    inner class AuthenticationStart : Transition<Login, AsyncWorkState.Authenticating>() {
        override fun transform(state: Login): AsyncWorkState.Authenticating {
            return AsyncWorkState.Authenticating(state.mail, state.password)
        }
    }
}