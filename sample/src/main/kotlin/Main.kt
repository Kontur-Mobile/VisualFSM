import authFSM.AuthFSMFeature
import authFSM.actions.Authenticate
import authFSM.actions.StartRegistration

fun main() {
    val authFeature = AuthFSMFeature()
    println("Demo FSM sample start")

    authFeature.proceed(Authenticate("", ""))
    authFeature.proceed(StartRegistration("", ""))

    println("Demo FSM sample end")
}