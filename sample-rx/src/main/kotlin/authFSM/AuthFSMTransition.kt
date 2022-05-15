package authFSM

import ru.kontur.mobile.visualfsm.Transition
import kotlin.reflect.KClass

abstract class AuthFSMTransition<FROM : AuthFSMState, TO : AuthFSMState>(
    override val fromState: KClass<FROM>,
    override val toState: KClass<TO>
) : Transition<FROM, TO>(fromState, toState)