package com.sagauxassignment.util

import android.content.Context
import android.widget.Toast

val emailRegex = Regex("^((([!#$%&'*+\\-/=?^_`{|}~\\w])|" +
        "([!#$%&'*+\\-/=?^_`{|}~\\w]+[!#$%&'*+\\-/=?^_`{|}~\\w]))[@]\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)$")

fun isValidEmail(email: String): Boolean {
    return emailRegex.matches(email)
}

val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")

fun isValidPassword(password: String): Boolean {
    return passwordRegex.matches(password)
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}