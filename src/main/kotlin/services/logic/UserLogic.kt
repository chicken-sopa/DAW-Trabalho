package services.logic

import org.springframework.stereotype.Component
import services.UserErrors

@Component
class UserLogic {

    fun validatePassword(password: String) {
        if(password.length !in 5..20)
            throw UserErrors.InvalidPassword("Invalid password Length! (5 < size < 20)")
        if (password.none { it.isLowerCase() })
            throw UserErrors.InvalidPassword("Password must contain lowercase letters!")
        if (password.none { it.isUpperCase() })
            throw UserErrors.InvalidPassword("Password must contain uppercase letters!")
        if (password.none { it.isDigit() })
            throw UserErrors.InvalidPassword("Password must contain numbers!")
    }

    fun validateUsername(username: String) {
        if (username.length !in 3..20)
            throw UserErrors.InvalidUsername("Invalid username size! (3 < size > 20)")
        if (username.any { !it.isLetterOrDigit() })
            throw UserErrors.InvalidUsername("Username must only contain letters and digits")
    }
}