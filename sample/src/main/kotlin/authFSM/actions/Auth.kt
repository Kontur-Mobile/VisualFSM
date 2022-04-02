package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition

class Auth() : AuthFSMAction() {
    inner class OnAuthStart :
        AuthFSMTransition<Login, AsyncWorkState.Checking>(
            Login::class,
            AsyncWorkState.Checking::class
        ) {
        override fun transform(state: Login): AsyncWorkState.Checking {
            return AsyncWorkState.Checking(state.mail, state.password)
        }
    }

    override val transitions = listOf(
        OnAuthStart(),
    )
}