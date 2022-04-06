package authFSM

import authFSM.actions.AuthFSMAction
import ru.kontur.mobile.visualfsm.Store
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class AuthFSMStore(callbacks: TransitionCallbacks<AuthFSMState>) : Store<AuthFSMState, AuthFSMAction>(
    AuthFSMState.Login("", ""),
    callbacks
)