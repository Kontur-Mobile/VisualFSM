import authFSM.AuthFSMFeature
import authFSM.actions.Authenticate

fun main() {
    val authFeature = AuthFSMFeature()
    println("Auth FSM sample start")

    authFeature.proceed(Authenticate("test@mail.com", "test"))

    println("Press ENTER to continue...")
    readln()
    println("Auth FSM sample end")
}

