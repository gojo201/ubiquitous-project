package com.tcsappdev.ubiquitous.utils

object AuthValidation{

    fun isValidEmail(email: String): Boolean{
        return email.isNotEmpty() && email.contains("@")
    }

    fun isValidPassword(password: String): Boolean{
        return password.length >= 6
    }

    fun passwordMatch(password: String, confirmPassword: String): Boolean{
        return password==confirmPassword
    }

    fun isValidName(name: String): Boolean{
        return name.isNotEmpty()
    }
}