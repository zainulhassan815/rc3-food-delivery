package org.dreamerslab.rc3.utils.formfields

import androidx.core.util.PatternsCompat
import org.dreamerslab.rc3.utils.form.CachedFormInput

enum class EmailError {
    Empty,
    Invalid
}

class Email(
    value: String,
    isPure: Boolean = true
) : CachedFormInput<String, EmailError>(value, isPure) {
    override fun validator(value: String): EmailError? = when {
        value.isBlank() -> EmailError.Empty
        !PatternsCompat.EMAIL_ADDRESS.matcher(value).matches() -> EmailError.Invalid
        else -> null
    }
}
