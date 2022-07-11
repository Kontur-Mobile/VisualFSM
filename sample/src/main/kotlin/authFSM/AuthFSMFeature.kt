package authFSM

import AuthInteractor
import authFSM.actions.AuthFSMAction
import ru.kontur.mobile.visualfsm.*
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory

@GenerateTransitionsFactory
class AuthFSMFeature : Feature<AuthFSMState, AuthFSMAction>(
    initialState = AuthFSMState.Login("", ""),
    asyncWorker = AuthFSMAsyncWorker(AuthInteractor()),
    transitionCallbacks = object : TransitionCallbacks<AuthFSMState> {
        override fun onActionLaunched(action: Action<AuthFSMState>, currentState: AuthFSMState) {
            println("onActionLaunched\naction=$action\ncurrentState=$currentState")
        }

        override fun onTransitionSelected(
            action: Action<AuthFSMState>,
            transition: Transition<AuthFSMState, AuthFSMState>,
            currentState: AuthFSMState,
        ) {
            println("onTransitionSelected\naction=$action\ntransition=$transition\ncurrentState=$currentState")
        }

        override fun onNewStateReduced(
            action: Action<AuthFSMState>,
            transition: Transition<AuthFSMState, AuthFSMState>,
            oldState: AuthFSMState,
            newState: AuthFSMState,
        ) {
            println("onNewStateReduced\naction=$action\ntransition=$transition\noldState=$oldState\nnewState=$newState")
        }

        override fun onNoTransitionError(action: Action<AuthFSMState>, currentState: AuthFSMState) {
            println("onNoTransitionError\naction=$action\ncurrentState=$currentState")
        }

        override fun onMultipleTransitionError(action: Action<AuthFSMState>, currentState: AuthFSMState) {
            println("onMultipleTransitionError\naction=$action\ncurrentState=$currentState")
        }
    },
    transitionsFactory = provideTransitionsFactory(),
)