import authFSM.AuthResult
import authFSM.RegistrationResult
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class AuthInteractor {
    fun check(mail: String, password: String): Single<AuthResult> {
        return Single.just(AuthResult.SUCCESS).delay(3, TimeUnit.SECONDS)
    }

    fun register(mail: String, password: String): Single<RegistrationResult> {
        return Single.just(RegistrationResult.SUCCESS).delay(3, TimeUnit.SECONDS)
    }
}