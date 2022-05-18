import authFSM.AuthFSMAsyncWorker
import authFSM.AuthFSMState
import authFSM.actions.Authenticate
import ru.kontur.mobile.visualfsm.*

fun main() {
    val authFeature = FeatureRx(
        AuthFSMState.Login("", ""),
        AuthFSMAsyncWorker(AuthInteractor()),
        object : TransitionCallbacks<AuthFSMState> {
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
    println("Auth FSM sample start")

    authFeature.proceed(Authenticate("test@mail.com", "test"))

    println("Press ENTER to continue...")
    readln()
    println("Auth FSM sample end")
}

