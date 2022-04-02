package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition
import authFSM.AuthResult

class HandleAuthResult(val result: AuthResult) : AuthFSMAction() {

    inner class OnSuccess :
        AuthFSMTransition<AsyncWorkState.Checking, UserAuthorized>(
            AsyncWorkState.Checking::class,
            UserAuthorized::class
        ) {
        override fun predicate(state: AsyncWorkState.Checking): Boolean {
            return result == AuthResult.SUCCESS
        }

        override fun transform(state: AsyncWorkState.Checking): UserAuthorized {
            return UserAuthorized(state.mail)
        }
    }

    inner class OnBadCredential :
        AuthFSMTransition<AsyncWorkState.Checking, Login>(
            AsyncWorkState.Checking::class,
            Login::class
        ) {
        override fun predicate(state: AsyncWorkState.Checking): Boolean {
            return result == AuthResult.BAD_CREDENTIAL
        }

        override fun transform(state: AsyncWorkState.Checking): Login {
            return Login(state.mail, state.password, "Bad credential")
        }
    }

    inner class OnConnectionFailed :
        AuthFSMTransition<AsyncWorkState.Checking, Login>(
            AsyncWorkState.Checking::class,
            Login::class
        ) {
        override fun predicate(state: AsyncWorkState.Checking): Boolean {
            return result == AuthResult.NO_INTERNET
        }

        override fun transform(state: AsyncWorkState.Checking): Login {
            return Login(state.mail, state.password, "No internet")
        }
    }

    override val transitions = listOf(
        OnSuccess(),
        OnBadCredential(),
        OnConnectionFailed(),
    )
}