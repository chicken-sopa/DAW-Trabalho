package services

import org.springframework.security.crypto.password.PasswordEncoder
import domain.User
import repository.TransactionManager
import services.interfaces.IUserServices
import services.logic.UserLogic
import services.logic.TokenLogic

sealed class UserErrors: Throwable() {
    object UserDoesNotExist: UserErrors()
    object InvalidUsernameOrPassword: UserErrors()
    object IncorrectPassword: UserErrors()
    object InvalidTokenFormat: UserErrors()

    data class InvalidPassword(override val message: String): UserErrors()
    data class InvalidUsername(override val message: String): UserErrors()

    object OperationErrors {
        object CouldNotCreateToken: UserErrors()
        object CouldNotCreateUser: UserErrors()
    }
}

class UsersService(
    private val transactionManager: TransactionManager,
    private val passwordEncoder: PasswordEncoder,
    private val userLogic: UserLogic,
    private val tokenLogic: TokenLogic,
): IUserServices {

    override fun createUser(username: String, password: String): Boolean {
        // Parameters Validation using Logic
        if (username.isBlank() || password.isBlank())
            throw UserErrors.InvalidUsernameOrPassword

        userLogic.validateUsername(username)
        userLogic.validatePassword(password)

        // Preparations
        val passwordHash = passwordEncoder.encode(password)
        val newUser = User(username, passwordHash)

        // Transaction where the DB accesses happen
        return transactionManager.run {
            val usersRepository = it.usersRepo

            if (usersRepository.getByUsername(username) != null)
                throw UserErrors.UserDoesNotExist

            if (!usersRepository.create(newUser))
                throw UserErrors.OperationErrors.CouldNotCreateUser

            return@run true
        }
    }

    override fun login(username: String, password: String): String {
        if (username.isBlank() || password.isBlank())
            throw UserErrors.InvalidUsernameOrPassword

        return transactionManager.run {
            val usersRepo = it.usersRepo
            val user: User = usersRepo.getByUsername(username)
                ?: throw UserErrors.UserDoesNotExist

            if (!passwordEncoder.matches(password, user.password_hash))
                throw UserErrors.IncorrectPassword

            val newToken = tokenLogic.generateToken()
            val hashedToken = tokenLogic.hashToken(newToken)

            if (!usersRepo.createToken(hashedToken, user.username))
                throw UserErrors.OperationErrors.CouldNotCreateToken

            return@run newToken
        }
    }

    override fun getUserByToken(token: String): User? {
        if (!tokenLogic.canBeToken(token))
            throw UserErrors.InvalidTokenFormat

        val tokenHash = tokenLogic.hashToken(token)

        return transactionManager.run {
            val usersRepository = it.usersRepo

            return@run usersRepository.getUserByToken(tokenHash)
        }
    }
}