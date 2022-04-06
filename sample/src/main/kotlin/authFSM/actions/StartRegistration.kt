package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition

class StartRegistration : AuthFSMAction() {
    inner class RegistrationStart :
        AuthFSMTransition<Registration, ConfirmationRequested>(
            Registration::class,
            ConfirmationRequested::class
        ) {
        override fun predicate(state: Registration): Boolean {
            return state.password == state.repeatedPassword
        }

        override fun transform(state: Registration): ConfirmationRequested {
            return ConfirmationRequested(state.mail, state.password)
        }
    }

    inner class ValidationFailed :
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
        RegistrationStart(),
        ValidationFailed(),
    )
}