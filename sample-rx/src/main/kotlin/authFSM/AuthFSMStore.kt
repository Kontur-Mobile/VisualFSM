package authFSM

import authFSM.actions.AuthFSMAction
import ru.kontur.mobile.visualfsm.StoreRx
import ru.kontur.mobile.visualfsm.TransitionCallbacks

class AuthFSMStore(callbacks: TransitionCallbacks<AuthFSMState>) : StoreRx<AuthFSMState, AuthFSMAction>(
    AuthFSMState.Login("", ""),
    callbacks
)