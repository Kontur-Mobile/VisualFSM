package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition
import authFSM.RegistrationResult

class HandleRegistrationResult(val result: RegistrationResult) : AuthFSMAction() {

    inner class Success :
        AuthFSMTransition<AsyncWorkState.Registering, Login>(
            AsyncWorkState.Registering::class,
            Login::class
        ) {
        override fun predicate(state: AsyncWorkState.Registering): Boolean {
            return result == RegistrationResult.SUCCESS
        }

        override fun transform(state: AsyncWorkState.Registering): Login {
            return Login(state.mail, state.password)
        }
    }

    inner class BadCredential :
        AuthFSMTransition<AsyncWorkState.Registering, Registration>(
            AsyncWorkState.Registering::class,
            Registration::class
        ) {
        override fun predicate(state: AsyncWorkState.Registering): Boolean {
            return result == RegistrationResult.BAD_CREDENTIAL
        }

        override fun transform(state: AsyncWorkState.Registering): Registration {
            return Registration(state.mail, state.password, "Bad credential")
        }
    }

    inner class ConnectionFailed :
        AuthFSMTransition<AsyncWorkState.Registering, Registration>(
            AsyncWorkState.Registering::class,
            Registration::class
        ) {
        override fun predicate(state: AsyncWorkState.Registering): Boolean {
            return result == RegistrationResult.NO_INTERNET
        }

        override fun transform(state: AsyncWorkState.Registering): Registration {
            return Registration(state.mail, state.password, state.password, "No internet")
        }
    }

    override val transitions = listOf(
        Success(),
        BadCredential(),
        ConnectionFailed(),
    )
}