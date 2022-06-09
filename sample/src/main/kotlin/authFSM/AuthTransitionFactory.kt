package authFSM

import authFSM.actions.*
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionFactory
import kotlin.reflect.KClass

class AuthTransitionFactory : TransitionFactory<AuthFSMState, AuthFSMAction> {

    private val cache = mutableMapOf<KClass<AuthFSMAction>, List<Transition<out AuthFSMState, out AuthFSMState>>>()

    override fun create(action: AuthFSMAction): List<Transition<out AuthFSMState, out AuthFSMState>> {
        return when (action) {
            is Authenticate -> TODO()
            is ChangeFlow -> TODO()
            is HandleAuthResult -> TODO()
            is HandleConfirmation -> TODO()
            is HandleRegistrationResult -> TODO()
            is StartRegistration -> TODO()
        }
    }
}