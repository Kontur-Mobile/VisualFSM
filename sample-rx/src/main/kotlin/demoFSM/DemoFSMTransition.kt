package demoFSM

import ru.kontur.mobile.visualfsm.Transition
import kotlin.reflect.KClass

abstract class DemoFSMTransition<FROM : DemoFSMState, TO : DemoFSMState>(
    override val fromState: KClass<FROM>,
    override val toState: KClass<TO>
) : Transition<FROM, TO>(fromState, toState)