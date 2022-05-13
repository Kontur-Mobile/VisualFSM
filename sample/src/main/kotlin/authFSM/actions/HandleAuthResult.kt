package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition
import authFSM.AuthResult

class HandleAuthResult(val result: AuthResult) : AuthFSMAction() {

    inner class Success :
        AuthFSMTransition<AsyncWorkState.Authenticating, UserAuthorized>(
            AsyncWorkState.Authenticating::class,
            UserAuthorized::class
        ) {
        override fun predicate(state: AsyncWorkState.Authenticating): Boolean {
            return result == AuthResult.SUCCESS
        }

        override fun transform(state: AsyncWorkState.Authenticating): UserAuthorized {
            return UserAuthorized(state.mail)
        }
    }

    inner class BadCredential :
        AuthFSMTransition<AsyncWorkState.Authenticating, Login>(
            AsyncWorkState.Authenticating::class,
            Login::class
        ) {
        override fun predicate(state: AsyncWorkState.Authenticating): Boolean {
            return result == AuthResult.BAD_CREDENTIAL
        }

        override fun transform(state: AsyncWorkState.Authenticating): Login {
            return Login(state.mail, state.password, "Bad credential")
        }
    }

    inner class ConnectionFailed :
        AuthFSMTransition<AsyncWorkState.Authenticating, Login>(
            AsyncWorkState.Authenticating::class,
            Login::class
        ) {
        override fun predicate(state: AsyncWorkState.Authenticating): Boolean {
            return result == AuthResult.NO_INTERNET
        }

        override fun transform(state: AsyncWorkState.Authenticating): Login {
            return Login(state.mail, state.password, "No internet")
        }
    }

    override fun getTransitions() = listOf(
        Success(),
        BadCredential(),
        ConnectionFailed(),
    )
}