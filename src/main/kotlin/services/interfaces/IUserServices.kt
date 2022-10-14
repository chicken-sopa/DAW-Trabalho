package services.interfaces

import domain.User
import java.util.UUID

/**
 * Create a User -> validate fields + generate token + give to user
 * Login -> validate credentials + generate token + give to user
 * From Token -> get user from token
 * */
interface IUserServices {

    // Allow an user to create a new user.
    fun createUser(username: String, password: String): UUID

    // Allow an user to login to his account.
    fun login(username: String, password: String): UUID

    fun getUserByToken(token: String): User?
}