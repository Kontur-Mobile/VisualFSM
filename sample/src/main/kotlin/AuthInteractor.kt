import authFSM.AuthResult
import authFSM.RegistrationResult

class AuthInteractor {
    suspend fun check(mail: String, password: String): AuthResult {
        return AuthResult.SUCCESS
    }

    suspend fun register(mail: String, password: String): RegistrationResult {
        return RegistrationResult.SUCCESS
    }
}