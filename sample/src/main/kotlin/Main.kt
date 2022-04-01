import demoFSM.DemoFSMFeature
import demoFSM.DemoFSMState
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks

fun main() {
    val demoFSMFeature = DemoFSMFeature(object :TransitionCallbacks<DemoFSMState> {
        override fun onActionLaunched(action: Action<DemoFSMState>, currentState: DemoFSMState) {
            println("onActionLaunched\naction=$action\ncurrentState=$currentState")
        }

        override fun onTransitionSelected(
            action: Action<DemoFSMState>,
            transition: Transition<DemoFSMState, DemoFSMState>,
            currentState: DemoFSMState
        ) {
            println("onTransitionSelected\naction=$action\ntransition=$transition\ncurrentState=$currentState")
        }

        override fun onNewStateReduced(
            action: Action<DemoFSMState>,
            transition: Transition<DemoFSMState, DemoFSMState>,
            oldState: DemoFSMState,
            newState: DemoFSMState
        ) {
            println("onNewStateReduced\naction=$action\ntransition=$transition\noldState=$oldState\nnewState=$newState")
        }

        override fun onNoTransitionError(action: Action<DemoFSMState>, currentState: DemoFSMState) {
            println("onNoTransitionError\naction=$action\ncurrentState=$currentState")
        }

        override fun onMultipleTransitionError(action: Action<DemoFSMState>, currentState: DemoFSMState) {
            println("onMultipleTransitionError\naction=$action\ncurrentState=$currentState")
        }
    })
    println("Demo FSM sample start")

    demoFSMFeature.inData()

    println("Demo FSM sample end")
}

