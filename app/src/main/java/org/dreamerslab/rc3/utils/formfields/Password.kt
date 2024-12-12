package org.dreamerslab.rc3.utils.formfields

import org.dreamerslab.rc3.utils.form.FormInput

enum class PasswordError {
    Empty,
    TooShort
}

class Password(
    value: String,
    isPure: Boolean = true
) : FormInput<String, PasswordError>(value, isPure) {
    override fun validator(value: String): PasswordError? = when {
        value.isBlank() -> PasswordError.Empty
        value.length < 8 -> PasswordError.TooShort
        else -> null
    }
}
