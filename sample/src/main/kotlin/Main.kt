import authFSM.AuthFeature
import authFSM.AuthFSMState
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks

fun main() {
    val authFeature = AuthFeature(object : TransitionCallbacks<AuthFSMState> {
        override fun onActionLaunched(action: Action<AuthFSMState>, currentState: AuthFSMState) {
            println("onActionLaunched\naction=$action\ncurrentState=$currentState")
        }

        override fun onTransitionSelected(
            action: Action<AuthFSMState>,
            transition: Transition<AuthFSMState, AuthFSMState>,
            currentState: AuthFSMState
        ) {
            println("onTransitionSelected\naction=$action\ntransition=$transition\ncurrentState=$currentState")
        }

        override fun onNewStateReduced(
            action: Action<AuthFSMState>,
            transition: Transition<AuthFSMState, AuthFSMState>,
            oldState: AuthFSMState,
            newState: AuthFSMState
        ) {
            println("onNewStateReduced\naction=$action\ntransition=$transition\noldState=$oldState\nnewState=$newState")
        }

        override fun onNoTransitionError(action: Action<AuthFSMState>, currentState: AuthFSMState) {
            println("onNoTransitionError\naction=$action\ncurrentState=$currentState")
        }

        override fun onMultipleTransitionError(action: Action<AuthFSMState>, currentState: AuthFSMState) {
            println("onMultipleTransitionError\naction=$action\ncurrentState=$currentState")
        }
    })
    println("Demo FSM sample start")

    authFeature.auth("", "")
    authFeature.registration("", "")

    println("Demo FSM sample end")
}