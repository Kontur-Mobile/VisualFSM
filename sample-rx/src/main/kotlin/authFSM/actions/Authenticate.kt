package authFSM.actions

import authFSM.AuthFSMState.*
import ru.kontur.mobile.visualfsm.Transition

class Authenticate(val mail: String, val password: String) : AuthFSMAction() {
    inner class AuthenticationStart :
        Transition<Login, AsyncWorkState.Authenticating>(
            Login::class,
            AsyncWorkState.Authenticating::class
        ) {
        override fun transform(state: Login): AsyncWorkState.Authenticating {
            return AsyncWorkState.Authenticating(state.mail, state.password)
        }
    }

    override fun getTransitions() = listOf(
        AuthenticationStart(),
    )
}