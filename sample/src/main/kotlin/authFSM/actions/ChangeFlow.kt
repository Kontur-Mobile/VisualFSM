package authFSM.actions

import authFSM.AuthFSMState
import authFSM.AuthFSMState.*
import authFSM.AuthFSMTransition
import authFSM.Flow
import ru.kontur.mobile.visualfsm.Edge

class ChangeFlow(val newFlow: Flow) : AuthFSMAction() {

    @Edge("ToLogin")
    inner class RegisterToLogin :
        AuthFSMTransition<Registration, Login>(
            Registration::class,
            Login::class
        ) {
        override fun transform(state: Registration): Login {
            return Login(state.mail, "")
        }
    }


    @Edge("ToRegistration")
    inner class LoginToRegistration :
        AuthFSMTransition<Login, Registration>(
            Login::class,
            Registration::class
        ) {
        override fun transform(state: Login): Registration {
            return Registration(state.mail, "", "")
        }
    }

    override val transitions = listOf(
        RegisterToLogin(),
        LoginToRegistration(),
    )
}