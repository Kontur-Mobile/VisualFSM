package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition

class HandleConfirmation(val confirmed: Boolean) : AuthFSMAction() {
    inner class Confirm :
        AuthFSMTransition<ConfirmationRequested, AsyncWorkState.Registering>(
            ConfirmationRequested::class, AsyncWorkState.Registering::class
        ) {
        override fun predicate(state: ConfirmationRequested): Boolean {
            return confirmed
        }

        override fun transform(state: ConfirmationRequested): AsyncWorkState.Registering {
            return AsyncWorkState.Registering(state.mail, state.password)
        }
    }

    inner class Cancel :
        AuthFSMTransition<ConfirmationRequested, Registration>(
            ConfirmationRequested::class, Registration::class
        ) {
        override fun predicate(state: ConfirmationRequested): Boolean {
            return !confirmed
        }

        override fun transform(state: ConfirmationRequested): Registration {
            return Registration(state.mail, state.password, state.password)
        }
    }

    override fun getTransitions() = listOf(
        Confirm(),
        Cancel(),
    )
}