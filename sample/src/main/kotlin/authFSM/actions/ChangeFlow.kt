package authFSM.actions

import authFSM.AuthFSMState.Login
import authFSM.AuthFSMState.Registration
import authFSM.Flow
import ru.kontur.mobile.visualfsm.Edge
import ru.kontur.mobile.visualfsm.Transition

class ChangeFlow(val newFlow: Flow) : AuthFSMAction() {

    @Edge("ToLogin")
    inner class RegisterToLogin : Transition<Registration, Login>() {
        override fun transform(state: Registration): Login {
            return Login(state.mail, "")
        }
    }


    @Edge("ToRegistration")
    inner class LoginToRegistration : Transition<Login, Registration>() {
        override fun transform(state: Login): Registration {
            return Registration(state.mail, "", "")
        }
    }
}