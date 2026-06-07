package com.tcsappdev.ubiquitous

import com.tcsappdev.ubiquitous.utils.AuthValidation
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class AuthValidationTest{

    @Test
    fun emptyEmailErr(){
        assertFalse(AuthValidation.isValidEmail(""))
    }

    @Test
    fun emptyPasswordErr(){
        assertFalse(AuthValidation.isValidPassword(""))
    }

    @Test
    fun validEmail(){
        assertTrue(AuthValidation.isValidEmail("test123@gmail.com"))
    }

    @Test
    fun validPassword(){
        assertTrue(AuthValidation.isValidPassword("password123"))
    }

    @Test
    fun passwordNotMatchErr() {
        assertFalse(AuthValidation.passwordMatch("password123", "password456"))
    }

    @Test
    fun passwordMatch() {
        assertTrue(AuthValidation.passwordMatch("password123", "password123"))

    }

    @Test
    fun emptyNameErr() {
        assertFalse(AuthValidation.isValidName(""))
    }

    @Test
    fun validName() {
        assertTrue(AuthValidation.isValidName("Test"))
    }

}