package authFSM.actions

import authFSM.AuthFSMState.ConfirmationRequested
import authFSM.AuthFSMState.Registration
import ru.kontur.mobile.visualfsm.Transition

class StartRegistration(val mail: String, val password: String) : AuthFSMAction() {
    inner class RegistrationStart : Transition<Registration, ConfirmationRequested>() {
        override fun predicate(state: Registration): Boolean {
            return state.password == state.repeatedPassword
        }

        override fun transform(state: Registration): ConfirmationRequested {
            return ConfirmationRequested(state.mail, state.password)
        }
    }

    inner class ValidationFailed : Transition<Registration, Registration>() {
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
}