package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition

class StartRegistration : AuthFSMAction() {
    inner class OnRegistrationStart :
        AuthFSMTransition<Registration, AsyncWorkState.Registering>(
            Registration::class,
            AsyncWorkState.Registering::class
        ) {
        override fun predicate(state: Registration): Boolean {
            return state.password == state.repeatedPassword
        }

        override fun transform(state: Registration): AsyncWorkState.Registering {
            return AsyncWorkState.Registering(state.mail, state.password)
        }
    }

    inner class OnValidationFailed :
        AuthFSMTransition<Registration, Registration>(
            Registration::class,
            Registration::class
        ) {
        override fun predicate(state: Registration): Boolean {
            return state.password != state.repeatedPassword
        }

        override fun transform(state: Registration): Registration {
            return Registration(
                state.mail,
                state.password,
                state.repeatedPassword,
                "Password and repeated password must be equals"
            )
        }
    }

    override val transitions = listOf(
        OnRegistrationStart(),
        OnValidationFailed(),
    )
}