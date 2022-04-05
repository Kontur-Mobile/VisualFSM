package authFSM.actions

import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition

class HandleConfirmation(val confirm: Boolean) : AuthFSMAction() {
    inner class OnConfirm :
        AuthFSMTransition<ConfirmationRequested, AsyncWorkState.Registering>(
            ConfirmationRequested::class, AsyncWorkState.Registering::class
        ) {
        override fun predicate(state: ConfirmationRequested): Boolean {
            return confirm
        }

        override fun transform(state: ConfirmationRequested): AsyncWorkState.Registering {
            return AsyncWorkState.Registering(state.mail, state.password)
        }
    }

    inner class OnCancel :
        AuthFSMTransition<ConfirmationRequested, Registration>(
            ConfirmationRequested::class, Registration::class
        ) {
        override fun predicate(state: ConfirmationRequested): Boolean {
            return !confirm
        }

        override fun transform(state: ConfirmationRequested): Registration {
            return Registration(state.mail, state.password, state.password)
        }
    }

    override val transitions = listOf(
        OnConfirm(),
        OnCancel(),
    )
}