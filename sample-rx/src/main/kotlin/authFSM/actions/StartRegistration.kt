package authFSM.actions

import authFSM.AuthFSMState.*
import ru.kontur.mobile.visualfsm.Transition

class StartRegistration(val mail: String, val password: String) : AuthFSMAction() {
    inner class RegistrationStart :
        Transition<Registration, ConfirmationRequested>(
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
        Transition<Registration, Registration>(
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

    override fun getTransitions() = listOf(
        RegistrationStart(),
        ValidationFailed(),
    )
}