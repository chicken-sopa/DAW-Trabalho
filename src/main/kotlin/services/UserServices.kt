package services

import domain.PasswordValidationInfo
import org.springframework.security.crypto.password.PasswordEncoder
import domain.User
import repository.TransactionManager
import services.interfaces.IUserServices
import utils.TokenEncoder
import java.lang.Error
import java.util.UUID

class UsersService(
    private val transactionManager: TransactionManager,
    private val passwordEncoder: PasswordEncoder,
    //private val tokenEncoder: TokenEncoder,
    /*
    private val userLogic: UserLogic,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    */
): IUserServices{

    override fun createUser(username: String, password: String): Boolean {

        User.validateUsername(username)
        User.validatePassword(password)

        val passwordHash = passwordEncoder.encode(password)
        val newUser = User(username, passwordHash)

        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.userExistsByUsername(username)) {
                throw Error("User already exists!")
            } else {

                usersRepository.createUser(newUser)

            }
        }
    }

    override fun login(username: String, password: String): UUID {
        TODO("Not yet implemented")
    }


    override fun createToken(username: String, password: String): UUID {
        if (username.isBlank() || password.isBlank()) {
            throw Error("User or Password invalid!")
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user: User = usersRepository.getUserByUsername(username) ?: throw Error("User Not Found")

            if (!passwordEncoder.matches(password, user.password_hash)) {
               throw Error("UserOrPasswordAreInvalid")
            }
            val token = UUID.randomUUID()
            if(!usersRepository.createToken(token, user.username)) throw Error("Can´t create token")
            return@run token
        }
    }

    override fun getUserByToken(token: String): User? {
        /*if (!userLogic.canBeToken(token)) {
            return null
        }*/

        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getUserByToken(token)

        }
    }

    /*private fun userNotFound(): TokenCreationResult {
        passwordEncoder.encode("changeit")
        return Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
    }*/


}