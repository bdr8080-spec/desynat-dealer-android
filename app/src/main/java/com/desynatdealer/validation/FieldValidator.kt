package com.desynatdealer.validation

object FieldValidator {

    fun isNameValid(name:String?): Boolean{
        return !name.isNullOrBlank() && name.length >= 20 && name.all { it.isLetter() }
    }
    fun isEmailValid(email: String?): Boolean {
        return !email.isNullOrBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    fun isPasswordValid(password: String?): Boolean {
        return !password.isNullOrBlank() && password.length >= 6
    }

    fun isPhoneValid(phone: String?): Boolean {
        return !phone.isNullOrBlank() && phone.length == 10 && phone.all { it.isDigit() }
    }

}
