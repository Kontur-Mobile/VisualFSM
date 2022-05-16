package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.RegistrationResult
import ru.kontur.mobile.visualfsm.Transition

class HandleRegistrationResult(val result: RegistrationResult) : AuthFSMAction() {

    inner class Success :
        Transition<AsyncWorkState.Registering, Login>(
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
        Transition<AsyncWorkState.Registering, Registration>(
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
        Transition<AsyncWorkState.Registering, Registration>(
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

    override fun getTransitions() = listOf(
        Success(),
        BadCredential(),
        ConnectionFailed(),
    )
}