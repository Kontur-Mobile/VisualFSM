import authFSM.AuthResult
import authFSM.RegistrationResult
import kotlinx.coroutines.delay

class AuthInteractor {
    suspend fun check(mail: String, password: String): AuthResult {
        delay(3000)
        return AuthResult.SUCCESS
    }

    suspend fun register(mail: String, password: String): RegistrationResult {
        delay(3000)
        return RegistrationResult.SUCCESS
    }
}