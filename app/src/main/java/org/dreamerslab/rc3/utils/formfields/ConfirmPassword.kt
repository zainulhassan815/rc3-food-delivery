package org.dreamerslab.rc3.utils.formfields

import org.dreamerslab.rc3.utils.form.FormInput

enum class ConfirmPasswordError {
    Empty,
    PasswordsNotSame
}

class ConfirmPassword(
    value: String,
    private val password: Password,
    isPure: Boolean = true
) : FormInput<String, ConfirmPasswordError>(value, isPure) {
    override fun validator(value: String): ConfirmPasswordError? = when {
        value.isBlank() -> ConfirmPasswordError.Empty
        password.value != value -> ConfirmPasswordError.PasswordsNotSame
        else -> null
    }
}
